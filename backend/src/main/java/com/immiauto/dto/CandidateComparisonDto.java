package com.immiauto.dto;

import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CandidateComparisonDto {
    private UUID id;
    private UUID caseId;
    private String candidateName;
    private String candidateType;
    private String qualifications;
    private int yearsExperience;
    private String educationLevel;
    private String languageSkills;
    private boolean interviewed;
    private String interviewNotes;
    private String outcome;
    private String nonHireReason;
    private int sortOrder;
}
