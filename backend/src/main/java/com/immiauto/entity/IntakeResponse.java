package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intake_responses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "intake_responses_gen")
    @SequenceGenerator(name = "intake_responses_gen", sequenceName = "intake_responses_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String sectionName; // e.g., "Personal Identity", "Education", "Financial"

    @Column(nullable = false)
    private String questionKey; // machine key e.g., "passport_number"

    @Column(nullable = false)
    private String questionLabel; // human-readable e.g., "Passport Number"

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column
    private int sortOrder;

    @Column
    private boolean flaggedForReview; // AI or consultant flagged this answer
}
