package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * A generated submission package for a case (Section 4.1 - Milestone 5).
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CasePackageDto {

    private UUID id;
    private UUID caseId;
    private UUID packageProfileId;
    private String profileCode;
    private String profileDisplayName;
    private String status;

    private int errorCount;
    private int warningCount;
    private int decisionCount;
    private int clientConfirmationCount;
    private int unresolvedEvidenceCount;
    private boolean approvalBlocked;

    private boolean hasZip;
    private String packageSha256;

    private String generatedAt;
    private String generatedBy;
    private String approvedAt;
    private String approvedBy;
    private String approvalNotes;
}
