package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * A single deterministic validation finding for a case + package profile.
 * Computed transiently for the readiness report (Section 4.1 - Phase E / Milestone 4);
 * persisted as PackageValidationIssue rows once a CasePackage exists (Milestone 5).
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ValidationIssueDto {

    private UUID id;             // set once persisted against a CasePackage (Milestone 5); null when transient
    private String severity;     // ERROR, WARNING, DECISION, CLIENT_CONFIRMATION, UNRESOLVED_EVIDENCE
    private String code;         // e.g. PASSPORT_EXPIRED (see §8)
    private String message;
    private String formCode;     // nullable - set for form-level issues
    private String fieldKey;     // nullable - canonical field key
    private String pdfFieldName; // nullable - PDF field
    private String sourceType;   // client, intake, document, travel history, work history, package, form
    private UUID sourceId;       // nullable - originating record id

    // Resolution state (persisted issues only)
    private boolean resolved;
    private String resolvedBy;
    private String resolvedAt;
    private String resolutionNotes;
}
