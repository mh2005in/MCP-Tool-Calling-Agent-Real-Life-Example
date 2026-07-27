package com.immiauto.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CandidateComparisonDto {
    private Long id;
    private Long caseId;
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
