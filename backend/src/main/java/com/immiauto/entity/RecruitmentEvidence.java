package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "recruitment_evidence")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentEvidence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String evidenceType; // JOB_AD, JOB_BANK_POSTING, INTERVIEW_RECORD, APPLICANT_SUMMARY, SCREENSHOT

    @Column(nullable = false)
    private String platform; // Job Bank, Indeed, LinkedIn, Company Website, etc.

    @Column(nullable = false)
    private LocalDate postingDate;

    @Column
    private LocalDate expiryDate;

    @Column
    private int daysPosted;

    @Column
    private int applicantsReceived;

    @Column
    private int interviewsConducted;

    @Column(columnDefinition = "TEXT")
    private String nonHireReasons;

    @Column
    private boolean screenshotAttached;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column
    private int sortOrder;
}
