package com.immiauto.service.forms;

import java.util.UUID;

import com.immiauto.dto.forms.*;
import com.immiauto.entity.Document;
import com.immiauto.entity.ImmigrationCase;
import com.immiauto.entity.TravelHistoryEntry;
import com.immiauto.entity.WorkHistoryEntry;
import com.immiauto.entity.forms.CaseFormDraft;
import com.immiauto.entity.forms.FormFieldDefinition;
import com.immiauto.entity.forms.PackageDocumentRequirement;
import com.immiauto.entity.forms.PackageProfile;
import com.immiauto.entity.forms.PackageProfileForm;
import com.immiauto.enums.DocumentStatus;
import com.immiauto.enums.DraftOrigin;
import com.immiauto.enums.PackageStatus;
import com.immiauto.enums.ValidationSeverity;
import com.immiauto.repository.CaseRepository;
import com.immiauto.repository.DocumentRepository;
import com.immiauto.repository.TravelHistoryRepository;
import com.immiauto.repository.WorkHistoryRepository;
import com.immiauto.repository.forms.CaseFormDraftRepository;
import com.immiauto.repository.forms.FormFieldDefinitionRepository;
import com.immiauto.repository.forms.PackageDocumentRequirementRepository;
import com.immiauto.repository.forms.PackageProfileFormRepository;
import com.immiauto.repository.forms.PackageProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Deterministic validation engine producing a readiness report for a case +
 * package profile across three layers: canonical data, mapped form fields, and
 * package documents. Results are transient (not persisted until a CasePackage
 * exists in Milestone 5). (Section 4.1 - Phase E / Milestone 4)
 */
@Service
@RequiredArgsConstructor
public class PackageValidationService {

    private static final long WORK_GAP_THRESHOLD_DAYS = 31;

    private final CaseRepository caseRepository;
    private final PackageProfileRepository packageProfileRepository;
    private final PackageProfileFormRepository packageProfileFormRepository;
    private final PackageDocumentRequirementRepository documentRequirementRepository;
    private final DocumentRepository documentRepository;
    private final CaseFormDraftRepository caseFormDraftRepository;
    private final TravelHistoryRepository travelHistoryRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final FormFieldDefinitionRepository fieldDefinitionRepository;
    private final FormAutomationService formAutomationService;

    @Transactional(readOnly = true)
    public PackageReadinessReportDto evaluate(UUID caseId, UUID packageProfileId) {
        ImmigrationCase imCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));
        PackageProfile profile = packageProfileRepository.findById(packageProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Package profile not found: " + packageProfileId));

        MappingPreviewDto preview = formAutomationService.previewMappings(caseId, packageProfileId);
        Map<String, CanonicalValueDto> snapshot = indexSnapshot(preview.getSnapshot());

        // Current (non-superseded) draft per form definition, for draft-aware rules.
        Map<UUID, CaseFormDraft> currentDraftByForm = currentDrafts(caseId);
        List<PackageProfileForm> profileForms =
                packageProfileFormRepository.findByPackageProfileIdOrderBySortOrder(packageProfileId);

        List<ValidationIssueDto> issues = new ArrayList<>();
        validateCanonical(caseId, imCase, preview.getSnapshot(), snapshot, issues);
        validateRequiredForms(profileForms, currentDraftByForm, issues);
        validateForms(preview, currentDraftByForm, issues);
        validatePackage(caseId, packageProfileId, issues);

        return buildReport(imCase, profile, issues);
    }

    /** Latest non-superseded draft keyed by form definition id. */
    private Map<UUID, CaseFormDraft> currentDrafts(UUID caseId) {
        Map<UUID, CaseFormDraft> byForm = new LinkedHashMap<>();
        for (CaseFormDraft d : caseFormDraftRepository.findByImmigrationCaseId(caseId)) {
            if (d.getStatus() == PackageStatus.SUPERSEDED) {
                continue;
            }
            UUID formId = d.getFormDefinition().getId();
            CaseFormDraft existing = byForm.get(formId);
            if (existing == null || (d.getGeneratedAt() != null && existing.getGeneratedAt() != null
                    && d.getGeneratedAt().isAfter(existing.getGeneratedAt()))) {
                byForm.put(formId, d);
            }
        }
        return byForm;
    }

    /** T2: every required form must have a current draft (generated or uploaded). */
    private void validateRequiredForms(List<PackageProfileForm> profileForms,
                                       Map<UUID, CaseFormDraft> currentDraftByForm,
                                       List<ValidationIssueDto> issues) {
        for (PackageProfileForm ppf : profileForms) {
            if (ppf.isRequired() && !currentDraftByForm.containsKey(ppf.getFormDefinition().getId())) {
                add(issues, ValidationSeverity.ERROR, "REQUIRED_FORM_NOT_PROVIDED",
                        "Required form " + ppf.getFormDefinition().getFormCode()
                                + " has no generated or uploaded draft.",
                        ppf.getFormDefinition().getFormCode(), null, null, "form",
                        ppf.getFormDefinition().getId());
            }
        }
    }

    // ---------------- Layer 1: canonical data ----------------

    private void validateCanonical(UUID caseId, ImmigrationCase imCase, CanonicalDataSnapshotDto fullSnapshot,
                                   Map<String, CanonicalValueDto> snapshot, List<ValidationIssueDto> issues) {
        requireField(snapshot, "primaryApplicant.fullName", "PRIMARY_NAME_REQUIRED",
                "Primary applicant full name is required.", ValidationSeverity.ERROR, issues);
        requireField(snapshot, "primaryApplicant.dateOfBirth", "DOB_REQUIRED",
                "Primary applicant date of birth is required.", ValidationSeverity.ERROR, issues);
        requireField(snapshot, "primaryApplicant.passport.number", "PASSPORT_NUMBER_REQUIRED",
                "Passport number is required.", ValidationSeverity.ERROR, issues);
        requireField(snapshot, "primaryApplicant.passport.expiryDate", "PASSPORT_EXPIRY_REQUIRED",
                "Passport expiry date is missing.", ValidationSeverity.WARNING, issues);

        // Passport expired before the expected submission date (case deadline, else today).
        String expiry = valueOf(snapshot, "primaryApplicant.passport.expiryDate");
        LocalDate refDate = imCase.getDeadline() != null ? imCase.getDeadline() : LocalDate.now();
        LocalDate expiryDate = parseDate(expiry);
        if (expiryDate != null && expiryDate.isBefore(refDate)) {
            add(issues, ValidationSeverity.ERROR, "PASSPORT_EXPIRED",
                    "Passport expires " + expiryDate + ", before the expected submission date " + refDate + ".",
                    null, "primaryApplicant.passport.expiryDate", null, "client", null);
        }

        // Consultant decisions: any conflicting / review-required canonical value.
        for (CanonicalValueDto v : fullSnapshot.getValues()) {
            if (v.isReviewRequired()) {
                add(issues, ValidationSeverity.DECISION, "CONSULTANT_DECISION_REQUIRED",
                        v.getDisplayName() + ": " + (v.isConflict() ? "sources disagree" : "review required") + ".",
                        null, v.getFieldKey(), null, "intake", null);
            }
        }

        // Travel history: exit before entry.
        for (TravelHistoryEntry t : travelHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId)) {
            if (t.getExitDate() != null && t.getEntryDate() != null && t.getExitDate().isBefore(t.getEntryDate())) {
                add(issues, ValidationSeverity.ERROR, "TRAVEL_EXIT_BEFORE_ENTRY",
                        "Travel to " + t.getCountry() + ": exit date " + t.getExitDate()
                                + " is before entry date " + t.getEntryDate() + ".",
                        null, "travelHistory.entries", null, "travel history", t.getId());
            }
        }

        // Work history: invalid date order and gaps.
        List<WorkHistoryEntry> work = new ArrayList<>(workHistoryRepository.findByImmigrationCaseIdOrderBySortOrder(caseId));
        for (WorkHistoryEntry w : work) {
            if (w.getStartDate() != null && w.getEndDate() != null && w.getEndDate().isBefore(w.getStartDate())) {
                add(issues, ValidationSeverity.ERROR, "DATE_ORDER_INVALID",
                        "Employment at " + w.getEmployerName() + ": end date " + w.getEndDate()
                                + " is before start date " + w.getStartDate() + ".",
                        null, "workHistory.entries", null, "work history", w.getId());
            }
        }
        detectWorkGaps(work, issues);
    }

    private void detectWorkGaps(List<WorkHistoryEntry> work, List<ValidationIssueDto> issues) {
        List<WorkHistoryEntry> sorted = work.stream()
                .filter(w -> w.getStartDate() != null)
                .sorted(Comparator.comparing(WorkHistoryEntry::getStartDate))
                .toList();
        for (int i = 1; i < sorted.size(); i++) {
            WorkHistoryEntry prev = sorted.get(i - 1);
            WorkHistoryEntry next = sorted.get(i);
            if (prev.getEndDate() == null || prev.isCurrentJob()) {
                continue;
            }
            long gap = ChronoUnit.DAYS.between(prev.getEndDate(), next.getStartDate());
            if (gap > WORK_GAP_THRESHOLD_DAYS) {
                add(issues, ValidationSeverity.WARNING, "WORK_HISTORY_GAP_DETECTED",
                        "Employment gap of " + gap + " days between " + prev.getEmployerName()
                                + " (ended " + prev.getEndDate() + ") and " + next.getEmployerName()
                                + " (started " + next.getStartDate() + ").",
                        null, "workHistory.entries", null, "work history", next.getId());
            }
        }
    }

    // ---------------- Layer 2: mapped form fields ----------------

    private void validateForms(MappingPreviewDto preview, Map<UUID, CaseFormDraft> currentDraftByForm,
                               List<ValidationIssueDto> issues) {
        for (FormMappingPreviewDto form : preview.getForms()) {
            // T3: a manually-uploaded draft satisfies this form; skip auto-fill field findings.
            CaseFormDraft current = currentDraftByForm.get(form.getFormDefinitionId());
            if (current != null && current.getOrigin() == DraftOrigin.UPLOADED) {
                continue;
            }
            if (form.getMappingStatus() == null) {
                add(issues, ValidationSeverity.WARNING, "PDF_FIELD_UNMAPPED",
                        "No approved mapping version exists for " + form.getFormCode() + "; fields cannot be filled.",
                        form.getFormCode(), null, null, "form", form.getFormDefinitionId());
                continue;
            }

            Map<String, Integer> maxLengths = new LinkedHashMap<>();
            for (FormFieldDefinition def : fieldDefinitionRepository
                    .findByFormDefinitionIdOrderByPageNumber(form.getFormDefinitionId())) {
                if (def.getMaxLength() != null) {
                    maxLengths.put(def.getPdfFieldName(), def.getMaxLength());
                }
            }

            for (MappedFieldPreviewDto f : form.getFields()) {
                if (f.isRequiredForPackage() && !f.isPresent()) {
                    add(issues, ValidationSeverity.ERROR, "PDF_REQUIRED_FIELD_EMPTY",
                            form.getFormCode() + " field '" + displayField(f) + "' is required but has no value.",
                            form.getFormCode(), f.getCanonicalFieldKey(), f.getPdfFieldName(), "form", null);
                }
                if ("UNSUPPORTED_TRANSFORM".equals(f.getStatus())) {
                    add(issues, ValidationSeverity.DECISION, "CONSULTANT_DECISION_REQUIRED",
                            form.getFormCode() + " field '" + displayField(f) + "' uses an unsupported transform; verify manually.",
                            form.getFormCode(), f.getCanonicalFieldKey(), f.getPdfFieldName(), "form", null);
                }
                Integer maxLen = maxLengths.get(f.getPdfFieldName());
                if (f.isPresent() && maxLen != null && f.getResolvedValue() != null
                        && f.getResolvedValue().length() > maxLen) {
                    add(issues, ValidationSeverity.WARNING, "PDF_VALUE_TOO_LONG",
                            form.getFormCode() + " field '" + displayField(f) + "' value exceeds max length " + maxLen + ".",
                            form.getFormCode(), f.getCanonicalFieldKey(), f.getPdfFieldName(), "form", null);
                }
            }

            for (String unmapped : form.getUnmappedRequiredFieldNames()) {
                add(issues, ValidationSeverity.WARNING, "PDF_FIELD_UNMAPPED",
                        form.getFormCode() + " required field '" + unmapped + "' has no mapping.",
                        form.getFormCode(), null, unmapped, "form", null);
            }
        }
    }

    // ---------------- Layer 3: package documents ----------------

    private void validatePackage(UUID caseId, UUID packageProfileId, List<ValidationIssueDto> issues) {
        List<Document> docs = documentRepository.findByImmigrationCaseId(caseId);

        for (PackageDocumentRequirement req : documentRequirementRepository
                .findByPackageProfileIdOrderBySortOrder(packageProfileId)) {
            List<Document> matches = docs.stream().filter(d -> matches(d, req)).toList();
            String label = StringUtils.hasText(req.getDocumentType()) ? req.getDocumentType() : req.getDocumentCategory();

            if (matches.isEmpty()) {
                if (req.isRequired()) {
                    add(issues, ValidationSeverity.ERROR, "DOCUMENT_REQUIRED_MISSING",
                            "Required document missing: " + label + ".",
                            null, null, null, "package", req.getId());
                }
            } else {
                for (Document d : matches) {
                    if (d.getStatus() == DocumentStatus.REJECTED || d.getStatus() == DocumentStatus.INCORRECT_DOCUMENT) {
                        add(issues, ValidationSeverity.ERROR, "DOCUMENT_REJECTED",
                                "Document '" + label + "' was rejected" + suffix(d.getRejectionReason()) + ".",
                                null, null, null, "document", d.getId());
                    } else if (isPending(d.getStatus())) {
                        add(issues, ValidationSeverity.WARNING, "DOCUMENT_PENDING_REVIEW",
                                "Document '" + label + "' is pending review (" + d.getStatus() + ").",
                                null, null, null, "document", d.getId());
                    }
                    if (d.isTranslationRequired() || d.getStatus() == DocumentStatus.TRANSLATION_REQUIRED) {
                        add(issues, ValidationSeverity.UNRESOLVED_EVIDENCE, "TRANSLATION_REQUIRED_UNRESOLVED",
                                "Document '" + label + "' requires a certified translation.",
                                null, null, null, "document", d.getId());
                    }
                }
            }

            if (StringUtils.hasText(req.getCertifiedCopyRule())) {
                add(issues, ValidationSeverity.CLIENT_CONFIRMATION, "CERTIFIED_COPY_REQUIRED_UNRESOLVED",
                        "Confirm certified copy requirement for '" + label + "': " + req.getCertifiedCopyRule(),
                        null, null, null, "package", req.getId());
            }
        }
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

    private boolean isPending(DocumentStatus status) {
        return status == DocumentStatus.UPLOADED
                || status == DocumentStatus.NEEDS_REVIEW
                || status == DocumentStatus.CONSULTANT_ACTION_NEEDED
                || status == DocumentStatus.CLIENT_ACTION_NEEDED;
    }

    // ---------------- report assembly ----------------

    private PackageReadinessReportDto buildReport(ImmigrationCase imCase, PackageProfile profile,
                                                  List<ValidationIssueDto> issues) {
        int errors = countBy(issues, ValidationSeverity.ERROR);
        return PackageReadinessReportDto.builder()
                .caseId(imCase.getId())
                .packageProfileId(profile.getId())
                .profileCode(profile.getProfileCode())
                .profileDisplayName(profile.getDisplayName())
                .generatedAt(LocalDateTime.now().toString())
                .errorCount(errors)
                .warningCount(countBy(issues, ValidationSeverity.WARNING))
                .decisionCount(countBy(issues, ValidationSeverity.DECISION))
                .clientConfirmationCount(countBy(issues, ValidationSeverity.CLIENT_CONFIRMATION))
                .unresolvedEvidenceCount(countBy(issues, ValidationSeverity.UNRESOLVED_EVIDENCE))
                .approvalBlocked(errors > 0)
                .issues(issues)
                .build();
    }

    private int countBy(List<ValidationIssueDto> issues, ValidationSeverity severity) {
        return (int) issues.stream().filter(i -> severity.name().equals(i.getSeverity())).count();
    }

    // ---------------- helpers ----------------

    private void requireField(Map<String, CanonicalValueDto> snapshot, String key, String code,
                              String message, ValidationSeverity severity, List<ValidationIssueDto> issues) {
        CanonicalValueDto v = snapshot.get(key);
        if (v == null || !v.isPresent()) {
            add(issues, severity, code, message, null, key, null, "canonical", null);
        }
    }

    private void add(List<ValidationIssueDto> issues, ValidationSeverity severity, String code, String message,
                     String formCode, String fieldKey, String pdfFieldName, String sourceType, UUID sourceId) {
        issues.add(ValidationIssueDto.builder()
                .severity(severity.name()).code(code).message(message)
                .formCode(formCode).fieldKey(fieldKey).pdfFieldName(pdfFieldName)
                .sourceType(sourceType).sourceId(sourceId)
                .build());
    }

    private Map<String, CanonicalValueDto> indexSnapshot(CanonicalDataSnapshotDto snapshot) {
        Map<String, CanonicalValueDto> byKey = new LinkedHashMap<>();
        if (snapshot != null && snapshot.getValues() != null) {
            for (CanonicalValueDto v : snapshot.getValues()) {
                byKey.put(v.getFieldKey(), v);
            }
        }
        return byKey;
    }

    private String valueOf(Map<String, CanonicalValueDto> snapshot, String key) {
        CanonicalValueDto v = snapshot.get(key);
        return v == null ? null : v.getValue();
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String displayField(MappedFieldPreviewDto f) {
        return StringUtils.hasText(f.getLabel()) ? f.getLabel() : f.getPdfFieldName();
    }

    private String suffix(String reason) {
        return StringUtils.hasText(reason) ? " (" + reason + ")" : "";
    }
}
