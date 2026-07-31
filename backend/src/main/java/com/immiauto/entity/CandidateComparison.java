package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate_comparisons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CandidateComparison extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String candidateType; // CANADIAN, FOREIGN

    @Column
    private String qualifications;

    @Column
    private int yearsExperience;

    @Column
    private String educationLevel;

    @Column
    private String languageSkills;

    @Column
    private boolean interviewed;

    @Column(columnDefinition = "TEXT")
    private String interviewNotes;

    @Column
    private String outcome; // HIRED, NOT_HIRED, WITHDREW, NO_RESPONSE

    @Column(columnDefinition = "TEXT")
    private String nonHireReason;

    @Column
    private int sortOrder;
}
