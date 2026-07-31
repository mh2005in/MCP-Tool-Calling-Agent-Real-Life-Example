package com.immiauto.entity;

import com.immiauto.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "checklist_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String category; // Identity, Civil Status, Education, Financial, etc.

    @Column(nullable = false)
    private String documentName; // e.g., "Passport biographical page"

    @Column(columnDefinition = "TEXT")
    private String description; // Plain-language explanation for the client

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(nullable = false)
    private boolean required = true;

    @Column
    private boolean conditional; // Only needed based on client circumstances

    @Column
    private String conditionDescription; // Why it might be needed

    @Column
    private int sortOrder;

    @Column(columnDefinition = "TEXT")
    private String consultantReviewNote;

    @Column
    private String assignedParty; // APPLICANT, HOST, SPONSOR, EMPLOYER

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document linkedDocument;
}
