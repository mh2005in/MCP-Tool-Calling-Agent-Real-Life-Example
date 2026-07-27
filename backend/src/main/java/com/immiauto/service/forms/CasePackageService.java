package com.immiauto.service.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.dto.forms.*;
import com.immiauto.entity.Document;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.forms.*;
import com.immiauto.enums.PackageStatus;
import com.immiauto.enums.ValidationSeverity;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.DocumentRepository;
import com.immiauto.repository.forms.*;
import com.immiauto.security.CurrentUserProvider;
import com.immiauto.service.CommonService;
import com.immiauto.service.forms.pdf.PdfFormEngine;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Assembles and approves submission packages: persists validation issues,
 * builds the package index/manifest, zips generated + uploaded artifacts, and
 * enforces the approval gate. (Section 4.1 - Milestone 5)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CasePackageService {

    private final CaseRepository caseRepository;
    private final PackageProfileRepository packageProfileRepository;
    private final PackageProfileFormRepository packageProfileFormRepository;
    private final PackageDocumentRequirementRepository documentRequirementRepository;
    private final CasePackageRepository casePackageRepository;
    private final PackageValidationIssueRepository issueRepository;
    private final CaseFormDraftRepository draftRepository;
    private final DocumentRepository documentRepository;
    private final PackageValidationService packageValidationService;
    private final FormStorageService storage;
    private final PdfFormEngine pdfFormEngine;
    private final CommonService commonService;
    private final CurrentUserProvider currentUserProvider;
    private final ObjectMapper objectMapper;

    @Value("${app.forms.max-generated-package-bytes:104857600}")
    private long maxPackageBytes;

    // ---------------- create / read ----------------

    @Transactional
    public CasePackageDto createOrRefreshPackage(Long caseId, Long packageProfileId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));
        PackageProfile profile = packageProfileRepository.findById(packageProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Package profile not found: " + packageProfileId));

        CasePackage pkg = activePackage(caseId, packageProfileId);
        if (pkg == null) {
            pkg = CasePackage.builder()
                    .immigrationCase(imCase).packageProfile(profile)
                    .status(PackageStatus.DRAFT).build();
        } else if (pkg.getStatus() == PackageStatus.APPROVED) {
            pkg.setStatus(PackageStatus.SUPERSEDED);
            casePackageRepository.save(pkg);
            pkg = CasePackage.builder()
                    .immigrationCase(imCase).packageProfile(profile)
                    .status(PackageStatus.DRAFT).build();
        }

        PackageReadinessReportDto readiness = packageValidationService.evaluate(caseId, packageProfileId);
        PackageIndexDto index = buildIndex(imCase, profile);

        pkg.setReadinessReportJson(toJson(readiness));
        pkg.setPackageIndexJson(toJson(index));
        pkg.setStatus(readiness.isApprovalBlocked()
                ? PackageStatus.VALIDATION_FAILED : PackageStatus.READY_FOR_APPROVAL);
        pkg.setGeneratedAt(LocalDateTime.now());
        pkg.setGeneratedBy(currentUserLabel());
        pkg = casePackageRepository.save(pkg);

        persistIssues(pkg, caseId, readiness.getIssues());

        audit(pkg, "PACKAGE_REFRESHED");
        return toDto(pkg);
    }

    @Transactional
    public CasePackageDto refreshPackage(Long caseId, Long packageId) {
        CasePackage pkg = requirePackage(caseId, packageId);
        return createOrRefreshPackage(caseId, pkg.getPackageProfile().getId());
    }

    @Transactional(readOnly = true)
    public List<CasePackageDto> listPackages(Long caseId) {
        return casePackageRepository.findByImmigrationCaseId(caseId).stream()
                .filter(p -> p.getStatus() != PackageStatus.SUPERSEDED)
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CasePackageDto getPackage(Long caseId, Long packageId) {
        return toDto(requirePackage(caseId, packageId));
    }

    @Transactional(readOnly = true)
    public PackageReadinessReportDto getReadinessReport(Long caseId, Long packageId) {
        CasePackage pkg = requirePackage(caseId, packageId);
        return buildReadinessFromPersisted(pkg);
    }

    @Transactional(readOnly = true)
    public PackageIndexDto getPackageIndex(Long caseId, Long packageId) {
        CasePackage pkg = requirePackage(caseId, packageId);
        return buildIndex(pkg.getImmigrationCase(), pkg.getPackageProfile());
    }

    // ---------------- issue resolution (T7) ----------------

    @Transactional
    public PackageReadinessReportDto resolveIssue(Long caseId, Long packageId, Long issueId, String notes) {
        CasePackage pkg = requirePackage(caseId, packageId);
        PackageValidationIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found: " + issueId));
        if (!issue.getCasePackage().getId().equals(pkg.getId())) {
            throw new EntityNotFoundException("Issue not found: " + issueId);
        }
        if (issue.getSeverity() == ValidationSeverity.ERROR) {
            throw new IllegalStateException("Errors cannot be manually resolved; fix the underlying data and refresh.");
        }
        issue.setResolved(true);
        issue.setResolvedBy(currentUserLabel());
        issue.setResolvedAt(LocalDateTime.now());
        issue.setResolutionNotes(notes);
        issueRepository.save(issue);
        audit(pkg, "PACKAGE_ISSUE_RESOLVED");
        return buildReadinessFromPersisted(pkg);
    }

    // ---------------- approval (T6) ----------------

    @Transactional
    public CasePackageDto approvePackage(Long caseId, Long packageId, String notes, boolean acknowledged) {
        CasePackage pkg = requirePackage(caseId, packageId);
        if (pkg.getStatus() == PackageStatus.APPROVED) {
            throw new IllegalStateException("Package is already approved.");
        }
        if (!acknowledged) {
            throw new IllegalStateException(
                    "Consultant acknowledgement is required: official instructions and any manually-prepared forms remain the consultant's responsibility.");
        }
        long unresolvedErrors = issueRepository
                .countByCasePackageIdAndSeverityAndResolvedFalse(pkg.getId(), ValidationSeverity.ERROR);
        if (unresolvedErrors > 0) {
            throw new IllegalStateException("Approval blocked: " + unresolvedErrors + " unresolved error(s) remain.");
        }

        try {
            buildZip(pkg);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to assemble package zip: " + e.getMessage(), e);
        }

        pkg.setStatus(PackageStatus.APPROVED);
        pkg.setApprovedAt(LocalDateTime.now());
        pkg.setApprovedBy(currentUserLabel());
        pkg.setApprovalNotes(notes);
        pkg = casePackageRepository.save(pkg);
        audit(pkg, "PACKAGE_APPROVED");
        return toDto(pkg);
    }

    /** Resolve an approved package for secured zip download after building it if needed. */
    @Transactional
    public CasePackage getPackageForDownload(Long caseId, Long packageId) {
        CasePackage pkg = requirePackage(caseId, packageId);
        if (!StringUtils.hasText(pkg.getPackageZipPath()) || !Files.exists(Paths.get(pkg.getPackageZipPath()))) {
            try {
                buildZip(pkg);
                casePackageRepository.save(pkg);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to assemble package zip: " + e.getMessage(), e);
            }
        }
        return pkg;
    }

    // ---------------- index / issues / zip ----------------

    private PackageIndexDto buildIndex(ImmigrationCase imCase, PackageProfile profile) {
        Map<Long, CaseFormDraft> drafts = currentDrafts(imCase.getId());
        List<PackageIndexDto.PackageIndexFormDto> forms = new ArrayList<>();
        for (PackageProfileForm ppf : packageProfileFormRepository.findByPackageProfileIdOrderBySortOrder(profile.getId())) {
            FormDefinition form = ppf.getFormDefinition();
            CaseFormDraft draft = drafts.get(form.getId());
            forms.add(PackageIndexDto.PackageIndexFormDto.builder()
                    .sortOrder(ppf.getSortOrder())
                    .formCode(form.getFormCode())
                    .displayName(form.getDisplayName())
                    .required(ppf.isRequired())
                    .provided(draft != null)
                    .origin(draft == null || draft.getOrigin() == null ? null : draft.getOrigin().name())
                    .status(draft == null ? null : draft.getStatus().name())
                    .fileName(draft == null ? null : fileNameOf(draft))
                    .draftId(draft == null ? null : draft.getId())
                    .build());
        }

        List<Document> docs = documentRepository.findByImmigrationCaseId(imCase.getId());
        List<PackageIndexDto.PackageIndexDocumentDto> documents = new ArrayList<>();
        for (PackageDocumentRequirement req : documentRequirementRepository.findByPackageProfileIdOrderBySortOrder(profile.getId())) {
            long matched = docs.stream().filter(d -> matches(d, req)).count();
            documents.add(PackageIndexDto.PackageIndexDocumentDto.builder()
                    .sortOrder(req.getSortOrder())
                    .documentCategory(req.getDocumentCategory())
                    .documentType(req.getDocumentType())
                    .required(req.isRequired())
                    .present(matched > 0)
                    .matchedCount((int) matched)
                    .namingPattern(req.getNamingPattern())
                    .translationRule(req.getTranslationRule())
                    .certifiedCopyRule(req.getCertifiedCopyRule())
                    .build());
        }

        return PackageIndexDto.builder()
                .caseId(imCase.getId())
                .profileCode(profile.getProfileCode())
                .profileDisplayName(profile.getDisplayName())
                .generatedAt(LocalDateTime.now().toString())
                .forms(forms).documents(documents)
                .build();
    }

    private void persistIssues(CasePackage pkg, Long caseId, List<ValidationIssueDto> issues) {
        issueRepository.deleteAll(issueRepository.findByCasePackageId(pkg.getId()));
        Map<String, CaseFormDraft> byFormCode = new HashMap<>();
        for (CaseFormDraft d : currentDrafts(caseId).values()) {
            byFormCode.put(d.getFormDefinition().getFormCode(), d);
        }
        for (ValidationIssueDto dto : issues) {
            CaseFormDraft draft = dto.getFormCode() == null ? null : byFormCode.get(dto.getFormCode());
            issueRepository.save(PackageValidationIssue.builder()
                    .casePackage(pkg)
                    .caseFormDraft(draft)
                    .severity(ValidationSeverity.valueOf(dto.getSeverity()))
                    .code(dto.getCode())
                    .message(dto.getMessage())
                    .fieldKey(dto.getFieldKey())
                    .pdfFieldName(dto.getPdfFieldName())
                    .sourceType(dto.getSourceType())
                    .sourceId(dto.getSourceId())
                    .resolved(false)
                    .build());
        }
    }

    private void buildZip(CasePackage pkg) throws IOException {
        ImmigrationCase imCase = pkg.getImmigrationCase();
        PackageProfile profile = pkg.getPackageProfile();
        String caseNumber = imCase.getCaseNumber();

        Path dir = storage.packageDir(caseNumber, pkg.getId());
        Files.createDirectories(dir);

        PackageIndexDto index = buildIndex(imCase, profile);
        byte[] indexBytes = toJson(index).getBytes();
        byte[] readinessBytes = (pkg.getReadinessReportJson() == null ? "{}" : pkg.getReadinessReportJson()).getBytes();

        Map<Long, CaseFormDraft> drafts = currentDrafts(imCase.getId());
        List<Map<String, Object>> manifest = new ArrayList<>();
        manifest.add(manifestEntry("package-index.json", indexBytes));
        manifest.add(manifestEntry("readiness-report.json", readinessBytes));

        Path zip = storage.packageZip(caseNumber, pkg.getId());
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip))) {
            writeEntry(zos, "package-index.json", indexBytes);
            writeEntry(zos, "readiness-report.json", readinessBytes);
            for (CaseFormDraft draft : drafts.values()) {
                if (draft.getDraftFilePath() == null) continue;
                Path file = Paths.get(draft.getDraftFilePath());
                if (!Files.exists(file)) continue;
                byte[] bytes = Files.readAllBytes(file);
                String entryName = "forms/" + fileNameOf(draft);
                writeEntry(zos, entryName, bytes);
                manifest.add(manifestEntry(entryName, bytes));
            }
            byte[] manifestBytes = toJson(manifest).getBytes();
            writeEntry(zos, "package-manifest.json", manifestBytes);
            Files.write(dir.resolve("package-manifest.json"), manifestBytes);
        }

        long size = Files.size(zip);
        if (size > maxPackageBytes) {
            Files.deleteIfExists(zip);
            throw new IOException("Package exceeds the maximum allowed size of " + (maxPackageBytes / 1024 / 1024) + "MB");
        }

        Files.write(dir.resolve("package-index.json"), indexBytes);
        pkg.setPackageManifestPath(dir.resolve("package-manifest.json").toString());
        pkg.setPackageZipPath(zip.toString());
        pkg.setPackageSha256(pdfFormEngine.sha256(zip));
    }

    // ---------------- helpers ----------------

    private CasePackage activePackage(Long caseId, Long packageProfileId) {
        return casePackageRepository.findByImmigrationCaseIdAndPackageProfileId(caseId, packageProfileId).stream()
                .filter(p -> p.getStatus() != PackageStatus.SUPERSEDED)
                .max(Comparator.comparing(CasePackage::getId))
                .orElse(null);
    }

    private CasePackage requirePackage(Long caseId, Long packageId) {
        CasePackage pkg = casePackageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found: " + packageId));
        if (!pkg.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Package not found: " + packageId);
        }
        return pkg;
    }

    private Map<Long, CaseFormDraft> currentDrafts(Long caseId) {
        Map<Long, CaseFormDraft> byForm = new LinkedHashMap<>();
        for (CaseFormDraft d : draftRepository.findByImmigrationCaseId(caseId)) {
            if (d.getStatus() == PackageStatus.SUPERSEDED) continue;
            Long formId = d.getFormDefinition().getId();
            CaseFormDraft existing = byForm.get(formId);
            if (existing == null || (d.getGeneratedAt() != null && existing.getGeneratedAt() != null
                    && d.getGeneratedAt().isAfter(existing.getGeneratedAt()))) {
                byForm.put(formId, d);
            }
        }
        return byForm;
    }

    private PackageReadinessReportDto buildReadinessFromPersisted(CasePackage pkg) {
        List<ValidationIssueDto> issues = new ArrayList<>();
        int err = 0, warn = 0, dec = 0, client = 0, evidence = 0;
        for (PackageValidationIssue i : issueRepository.findByCasePackageId(pkg.getId())) {
            issues.add(toIssueDto(i));
            if (!i.isResolved()) {
                switch (i.getSeverity()) {
                    case ERROR -> err++;
                    case WARNING -> warn++;
                    case DECISION -> dec++;
                    case CLIENT_CONFIRMATION -> client++;
                    case UNRESOLVED_EVIDENCE -> evidence++;
                }
            }
        }
        return PackageReadinessReportDto.builder()
                .caseId(pkg.getImmigrationCase().getId())
                .packageProfileId(pkg.getPackageProfile().getId())
                .profileCode(pkg.getPackageProfile().getProfileCode())
                .profileDisplayName(pkg.getPackageProfile().getDisplayName())
                .generatedAt(pkg.getGeneratedAt() == null ? null : pkg.getGeneratedAt().toString())
                .errorCount(err).warningCount(warn).decisionCount(dec)
                .clientConfirmationCount(client).unresolvedEvidenceCount(evidence)
                .approvalBlocked(err > 0)
                .issues(issues)
                .build();
    }

    private ValidationIssueDto toIssueDto(PackageValidationIssue i) {
        String formCode = i.getCaseFormDraft() != null && i.getCaseFormDraft().getFormDefinition() != null
                ? i.getCaseFormDraft().getFormDefinition().getFormCode() : null;
        return ValidationIssueDto.builder()
                .id(i.getId())
                .severity(i.getSeverity().name())
                .code(i.getCode())
                .message(i.getMessage())
                .formCode(formCode)
                .fieldKey(i.getFieldKey())
                .pdfFieldName(i.getPdfFieldName())
                .sourceType(i.getSourceType())
                .sourceId(i.getSourceId())
                .resolved(i.isResolved())
                .resolvedBy(i.getResolvedBy())
                .resolvedAt(i.getResolvedAt() == null ? null : i.getResolvedAt().toString())
                .resolutionNotes(i.getResolutionNotes())
                .build();
    }

    private CasePackageDto toDto(CasePackage pkg) {
        PackageReadinessReportDto r = buildReadinessFromPersisted(pkg);
        return CasePackageDto.builder()
                .id(pkg.getId())
                .caseId(pkg.getImmigrationCase().getId())
                .packageProfileId(pkg.getPackageProfile().getId())
                .profileCode(pkg.getPackageProfile().getProfileCode())
                .profileDisplayName(pkg.getPackageProfile().getDisplayName())
                .status(pkg.getStatus().name())
                .errorCount(r.getErrorCount()).warningCount(r.getWarningCount())
                .decisionCount(r.getDecisionCount()).clientConfirmationCount(r.getClientConfirmationCount())
                .unresolvedEvidenceCount(r.getUnresolvedEvidenceCount())
                .approvalBlocked(r.isApprovalBlocked())
                .hasZip(StringUtils.hasText(pkg.getPackageZipPath()))
                .packageSha256(pkg.getPackageSha256())
                .generatedAt(pkg.getGeneratedAt() == null ? null : pkg.getGeneratedAt().toString())
                .generatedBy(pkg.getGeneratedBy())
                .approvedAt(pkg.getApprovedAt() == null ? null : pkg.getApprovedAt().toString())
                .approvedBy(pkg.getApprovedBy())
                .approvalNotes(pkg.getApprovalNotes())
                .build();
    }

    private boolean matches(Document d, PackageDocumentRequirement req) {
        if (StringUtils.hasText(req.getDocumentType()) && StringUtils.hasText(d.getDocumentType())) {
            return d.getDocumentType().equalsIgnoreCase(req.getDocumentType());
        }
        if (StringUtils.hasText(req.getDocumentCategory()) && StringUtils.hasText(d.getDocumentCategory())) {
            return d.getDocumentCategory().equalsIgnoreCase(req.getDocumentCategory());
        }
        return false;
    }

    private String fileNameOf(CaseFormDraft draft) {
        if (StringUtils.hasText(draft.getOriginalFileName())) {
            return draft.getFormDefinition().getFormCode() + "_" + draft.getOriginalFileName();
        }
        return draft.getDraftFilePath() == null ? null
                : Paths.get(draft.getDraftFilePath()).getFileName().toString();
    }

    private Map<String, Object> manifestEntry(String name, byte[] bytes) throws IOException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("bytes", bytes.length);
        m.put("sha256", sha256(bytes));
        return m;
    }

    private String sha256(byte[] bytes) throws IOException {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(bytes);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 unavailable", e);
        }
    }

    private void writeEntry(ZipOutputStream zos, String name, byte[] bytes) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(bytes);
        zos.closeEntry();
    }

    private void audit(CasePackage pkg, String action) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("caseId", pkg.getImmigrationCase().getId());
        details.put("packageProfile", pkg.getPackageProfile().getProfileCode());
        details.put("status", pkg.getStatus().name());
        details.put("packageSha256", pkg.getPackageSha256());
        commonService.logAudit("CasePackage", pkg.getId(), action, toJson(details));
    }

    private String currentUserLabel() {
        try {
            var user = currentUserProvider.getCurrentUser();
            return StringUtils.hasText(user.getEmail()) ? user.getEmail() : user.getDisplayName();
        } catch (Exception e) {
            return "system";
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.warn("Failed to serialize JSON: {}", e.getMessage());
            return null;
        }
    }
}
