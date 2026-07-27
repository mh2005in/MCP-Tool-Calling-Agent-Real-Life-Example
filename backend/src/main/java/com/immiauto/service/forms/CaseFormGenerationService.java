package com.immiauto.service.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.dto.forms.CanonicalDataSnapshotDto;
import com.immiauto.dto.forms.CaseFormDraftDto;
import com.immiauto.entity.AppUser;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.forms.CaseFormDraft;
import com.immiauto.entity.forms.FormDefinition;
import com.immiauto.entity.forms.FormMappingVersion;
import com.immiauto.entity.forms.PackageProfile;
import com.immiauto.entity.forms.PackageProfileForm;
import com.immiauto.enums.DraftOrigin;
import com.immiauto.enums.FormStatus;
import com.immiauto.enums.MappingStatus;
import com.immiauto.enums.PackageStatus;
import com.immiauto.mapper.CaseFormDraftMapper;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.forms.CaseFormDraftRepository;
import com.immiauto.repository.forms.FormDefinitionRepository;
import com.immiauto.repository.forms.FormMappingVersionRepository;
import com.immiauto.repository.forms.PackageProfileFormRepository;
import com.immiauto.repository.forms.PackageProfileRepository;
import com.immiauto.security.CurrentUserProvider;
import com.immiauto.service.CommonService;
import com.immiauto.service.forms.pdf.PdfFillResult;
import com.immiauto.service.forms.pdf.PdfFormEngine;
import com.immiauto.util.CommonUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Generates draft IRCC form PDFs for a case by applying an approved mapping
 * version to the canonical snapshot and filling the governed source PDF.
 * Captures input snapshot, mapped values, output hash, and an audit trail.
 * (Section 4.1 - Phase F / Milestone 3)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseFormGenerationService {

    private static final String AUDIT_GENERATED = "FORM_DRAFT_GENERATED";
    private static final String AUDIT_VALIDATED = "FORM_DRAFT_VALIDATED";
    private static final String AUDIT_SUPERSEDED = "FORM_DRAFT_SUPERSEDED";

    private static final String AUDIT_UPLOADED = "FORM_DRAFT_UPLOADED";
    private static final Set<String> UPLOAD_ALLOWED_EXTENSIONS = Set.of("pdf");

    private final CaseRepository caseRepository;
    private final PackageProfileRepository packageProfileRepository;
    private final PackageProfileFormRepository packageProfileFormRepository;
    private final CaseFormDraftRepository draftRepository;
    private final FormMappingVersionRepository mappingVersionRepository;
    private final FormDefinitionRepository formDefinitionRepository;
    private final CanonicalApplicantDataService canonicalDataService;
    private final FormMappingService formMappingService;
    private final PdfFormEngine pdfFormEngine;
    private final FormStorageService storage;
    private final CommonService commonService;
    private final CurrentUserProvider currentUserProvider;
    private final CaseFormDraftMapper draftMapper;
    private final ObjectMapper objectMapper;

    @Value("${app.forms.max-upload-bytes:26214400}")
    private long maxUploadBytes;

    /**
     * Generate (or regenerate) draft forms for every fillable form in a package profile.
     * Forms that are not fillable, lack an approved mapping, or whose source PDF is
     * missing are skipped (logged); a source-hash mismatch produces a VALIDATION_FAILED draft.
     */
    @Transactional
    public List<CaseFormDraftDto> generateDraftForms(Long caseId, Long packageProfileId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));
        PackageProfile profile = packageProfileRepository.findById(packageProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Package profile not found: " + packageProfileId));

        CanonicalDataSnapshotDto snapshot = canonicalDataService.buildSnapshot(caseId);
        String snapshotJson = toJson(snapshot);
        String user = currentUserLabel();

        List<CaseFormDraftDto> result = new ArrayList<>();
        for (PackageProfileForm ppf : packageProfileFormRepository.findByPackageProfileIdOrderBySortOrder(packageProfileId)) {
            FormDefinition form = ppf.getFormDefinition();
            Path outputDir = storage.generatedFormsDir(imCase.getCaseNumber(), profile.getId());
            CaseFormDraft draft = generateOne(imCase, form, snapshot, snapshotJson, user, outputDir);
            if (draft != null) {
                result.add(draftMapper.toDto(draft));
            }
        }
        return result;
    }

    /**
     * Record a manually-filled official form uploaded by the consultant (for forms
     * that cannot be auto-filled, e.g. XFA/certified IRCC PDFs). Stored as an
     * UPLOADED draft tied to the case + form, superseding any prior draft for that form.
     */
    @Transactional
    public CaseFormDraftDto uploadFilledForm(Long caseId, Long formDefinitionId, MultipartFile file) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));
        FormDefinition form = formDefinitionRepository.findById(formDefinitionId)
                .orElseThrow(() -> new EntityNotFoundException("Form not found: " + formDefinitionId));

        try {
            CommonUtil.validateUpload(file, UPLOAD_ALLOWED_EXTENSIONS, maxUploadBytes);

            supersedePriorDrafts(imCase.getId(), form.getId());

            String user = currentUserLabel();
            CaseFormDraft draft = draftRepository.save(CaseFormDraft.builder()
                    .immigrationCase(imCase)
                    .formDefinition(form)
                    .origin(DraftOrigin.UPLOADED)
                    .status(PackageStatus.READY_FOR_APPROVAL)
                    .originalFileName(CommonUtil.sanitizeFilename(file.getOriginalFilename()))
                    .generatedAt(LocalDateTime.now())
                    .generatedBy(user)
                    .build());

            Path output = storage.uploadedFormFile(imCase.getCaseNumber(), form.getFormCode(), draft.getId());
            Files.createDirectories(output.getParent());
            Files.copy(file.getInputStream(), output);

            draft.setDraftFilePath(output.toString());
            draft.setDraftSha256(pdfFormEngine.sha256(output));
            draft = draftRepository.save(draft);

            audit(draft, form, AUDIT_UPLOADED);
            return draftMapper.toDto(draft);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store uploaded form for " + form.getFormCode() + ": " + e.getMessage(), e);
        }
    }

    @Transactional
    public CaseFormDraftDto regenerateDraftForm(Long caseId, Long draftId) {
        CaseFormDraft existing = requireDraft(caseId, draftId);
        FormDefinition form = existing.getFormDefinition();

        CanonicalDataSnapshotDto snapshot = canonicalDataService.buildSnapshot(caseId);
        // Regenerate into the same folder the prior draft used (keeps package grouping).
        Path outputDir = existing.getDraftFilePath() != null
                ? Paths.get(existing.getDraftFilePath()).getParent()
                : storage.generatedFormsDir(existing.getImmigrationCase().getCaseNumber(), 0L);

        CaseFormDraft draft = generateOne(existing.getImmigrationCase(), form, snapshot,
                toJson(snapshot), currentUserLabel(), outputDir);
        if (draft == null) {
            throw new IllegalStateException("Form " + form.getFormCode()
                    + " cannot be regenerated (not fillable, no approved mapping, or missing source PDF).");
        }
        return draftMapper.toDto(draft);
    }

    @Transactional(readOnly = true)
    public List<CaseFormDraftDto> listDrafts(Long caseId) {
        return draftRepository.findByImmigrationCaseId(caseId).stream()
                .sorted(Comparator.comparing(CaseFormDraft::getGeneratedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(draftMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseFormDraftDto getFormDraft(Long caseId, Long draftId) {
        return draftMapper.toDto(requireDraft(caseId, draftId));
    }

    /**
     * Resolve a draft for download after verifying case ownership and that the file exists.
     * Returns the entity so the controller can stream the file through a secured endpoint.
     */
    @Transactional(readOnly = true)
    public CaseFormDraft getDraftForDownload(Long caseId, Long draftId) {
        CaseFormDraft draft = requireDraft(caseId, draftId);
        if (!StringUtils.hasText(draft.getDraftFilePath())
                || !Files.exists(Paths.get(draft.getDraftFilePath()))) {
            throw new EntityNotFoundException("Generated file not available for draft " + draftId);
        }
        return draft;
    }

    // ------------------------------------------------------------------

    private CaseFormDraft generateOne(ImmigrationCase imCase, FormDefinition form,
                                      CanonicalDataSnapshotDto snapshot, String snapshotJson,
                                      String user, Path outputDir) {
        if (!form.isSupportsFill() || form.getStatus() == FormStatus.BLOCKED) {
            log.info("Skipping form {} for case {}: fill not supported / blocked", form.getFormCode(), imCase.getId());
            return null;
        }
        Optional<FormMappingVersion> approved = mappingVersionRepository
                .findFirstByFormDefinitionIdAndStatusOrderByMappingVersionDesc(form.getId(), MappingStatus.APPROVED);
        if (approved.isEmpty()) {
            log.info("Skipping form {} for case {}: no approved mapping version", form.getFormCode(), imCase.getId());
            return null;
        }
        Path source = storage.sourcePdf(form.getFormCode(), form.getEditionLabel());
        if (!Files.exists(source)) {
            log.warn("Skipping form {} for case {}: source PDF not found at {}", form.getFormCode(), imCase.getId(), source);
            return null;
        }

        try {
            // Source-hash governance: only enforce when a governed hash has been recorded.
            if (StringUtils.hasText(form.getSourceSha256())) {
                String actual = pdfFormEngine.sha256(source);
                if (!actual.equalsIgnoreCase(form.getSourceSha256())) {
                    return persistHashMismatch(imCase, form, approved.get(), snapshotJson, user, actual);
                }
            }

            Map<String, String> values = formMappingService.resolveValues(form, snapshot);

            supersedePriorDrafts(imCase.getId(), form.getId());

            // Persist first to obtain the id used in the file name.
            CaseFormDraft draft = draftRepository.save(CaseFormDraft.builder()
                    .immigrationCase(imCase)
                    .formDefinition(form)
                    .mappingVersion(approved.get())
                    .origin(DraftOrigin.GENERATED)
                    .status(PackageStatus.DRAFT)
                    .inputSnapshotJson(snapshotJson)
                    .mappedValuesJson(toJson(values))
                    .generatedAt(LocalDateTime.now())
                    .generatedBy(user)
                    .build());

            Path output = outputDir.resolve(
                    storage.generatedFileName(form.getFormCode(), form.getEditionLabel(), draft.getId()));
            PdfFillResult fill = pdfFormEngine.fill(source, values, output);

            draft.setDraftFilePath(fill.getOutputPath());
            draft.setDraftSha256(fill.getSha256());
            draft.setValidationSummaryJson(toJson(Map.of(
                    "filledCount", fill.getFilledCount(),
                    "unmatchedFieldNames", fill.getUnmatchedFieldNames())));
            draft = draftRepository.save(draft);

            audit(draft, form, AUDIT_GENERATED);
            return draft;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate draft for " + form.getFormCode() + ": " + e.getMessage(), e);
        }
    }

    private CaseFormDraft persistHashMismatch(ImmigrationCase imCase, FormDefinition form,
                                              FormMappingVersion mv, String snapshotJson,
                                              String user, String actualHash) {
        supersedePriorDrafts(imCase.getId(), form.getId());
        CaseFormDraft draft = draftRepository.save(CaseFormDraft.builder()
                .immigrationCase(imCase)
                .formDefinition(form)
                .mappingVersion(mv)
                .origin(DraftOrigin.GENERATED)
                .status(PackageStatus.VALIDATION_FAILED)
                .inputSnapshotJson(snapshotJson)
                .validationSummaryJson(toJson(Map.of(
                        "code", "FORM_SOURCE_HASH_MISMATCH",
                        "expected", form.getSourceSha256(),
                        "actual", actualHash,
                        "message", "Source PDF hash does not match the governed mapping version.")))
                .generatedAt(LocalDateTime.now())
                .generatedBy(user)
                .build());
        audit(draft, form, AUDIT_VALIDATED);
        log.warn("Form {} for case {} blocked: FORM_SOURCE_HASH_MISMATCH", form.getFormCode(), imCase.getId());
        return draft;
    }

    private void supersedePriorDrafts(Long caseId, Long formDefinitionId) {
        for (CaseFormDraft prior : draftRepository.findByImmigrationCaseIdAndFormDefinitionId(caseId, formDefinitionId)) {
            if (prior.getStatus() == PackageStatus.APPROVED || prior.getStatus() == PackageStatus.SUPERSEDED) {
                continue;
            }
            prior.setStatus(PackageStatus.SUPERSEDED);
            draftRepository.save(prior);
            audit(prior, prior.getFormDefinition(), AUDIT_SUPERSEDED);
        }
    }

    private void audit(CaseFormDraft draft, FormDefinition form, String action) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("caseId", draft.getImmigrationCase().getId());
        details.put("formCode", form.getFormCode());
        details.put("formSourceSha256", form.getSourceSha256());
        details.put("mappingVersion", draft.getMappingVersion() == null ? null : draft.getMappingVersion().getMappingVersion());
        details.put("outputSha256", draft.getDraftSha256());
        details.put("status", draft.getStatus().name());
        commonService.logAudit("CaseFormDraft", draft.getId(), action, toJson(details));
    }

    private CaseFormDraft requireDraft(Long caseId, Long draftId) {
        CaseFormDraft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Draft not found: " + draftId));
        if (!draft.getImmigrationCase().getId().equals(caseId)) {
            throw new EntityNotFoundException("Draft not found: " + draftId);
        }
        return draft;
    }

    private String currentUserLabel() {
        try {
            AppUser user = currentUserProvider.getCurrentUser();
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
