package com.immiauto.entity;

import com.immiauto.enums.ApplicantRole;
import com.immiauto.enums.CaseStatus;
import com.immiauto.enums.CaseSubtype;
import com.immiauto.enums.LeadStatus;
import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "immigration_cases")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImmigrationCase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "immigration_cases_gen")
    @SequenceGenerator(name = "immigration_cases_gen", sequenceName = "immigration_cases_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 15)
    private String caseNumber;

    @PrePersist
    private void generateCaseNumber() {
        if (caseNumber == null) {
            caseNumber = "CS" + String.format("%013d", id);
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column
    private CaseSubtype subtype;

    @Enumerated(EnumType.STRING)
    @Column
    private ApplicantRole applicantRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus leadStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus caseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    @Column(columnDefinition = "TEXT")
    private String intakeSummary;

    @Column(columnDefinition = "TEXT")
    private String consultantNotes;

    @Column
    private LocalDate deadline;

    @Column
    private String urgencyReason;

    // --- Post-submission tracking ---
    @Column
    private LocalDate submissionDate;

    @Column
    private String applicationNumber;

    @Column
    private String portalUsed;

    @Column
    private String biometricsStatus;

    @Column
    private String medicalStatus;

    // --- Retainer tracking ---
    @Column
    private LocalDate retainerSignedDate;

    @Column
    private String retainerDocumentPath;

    @Column
    private String engagementLetterStatus; // DRAFT, SENT, SIGNED, EXPIRED

    // --- Final review / sign-off ---
    @Column
    private String finalReviewedBy;

    @Column
    private LocalDateTime finalReviewedAt;

    @Column(columnDefinition = "TEXT")
    private String finalReviewNotes;

    @Column
    private boolean consultantSignedOff;

    @OneToMany(mappedBy = "immigrationCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "immigrationCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "immigrationCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @OneToMany(mappedBy = "immigrationCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<IntakeResponse> intakeResponses = new ArrayList<>();
}
