package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic readiness report for a case + package profile: a flat list of
 * validation issues plus per-severity counts. {@code approvalBlocked} is true
 * while any ERROR remains. (Section 4.1 - Phase E / Milestone 4)
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PackageReadinessReportDto {

    private Long caseId;
    private Long packageProfileId;
    private String profileCode;
    private String profileDisplayName;
    private String generatedAt; // ISO-8601 timestamp

    private int errorCount;
    private int warningCount;
    private int decisionCount;
    private int clientConfirmationCount;
    private int unresolvedEvidenceCount;

    private boolean approvalBlocked;

    @Builder.Default
    private List<ValidationIssueDto> issues = new ArrayList<>();
}
