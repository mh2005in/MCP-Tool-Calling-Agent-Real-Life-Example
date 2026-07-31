package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * A generated draft form record for a case (Section 4.1 - Phase F).
 * File paths are intentionally not exposed; downloads go through a secured endpoint.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CaseFormDraftDto {

    private UUID id;
    private UUID caseId;
    private UUID formDefinitionId;
    private String formCode;
    private String formDisplayName;
    private UUID mappingVersionId;
    private Integer mappingVersion;
    private String origin;              // GENERATED, UPLOADED, DATA_SHEET
    private String status;
    private String draftSha256;
    private String fileName;            // display name of the stored file (no path)
    private String originalFileName;    // consultant's original file name (uploads)
    private String generatedAt;
    private String generatedBy;
    private String approvedAt;
    private String approvedBy;
    private String approvalNotes;
}
