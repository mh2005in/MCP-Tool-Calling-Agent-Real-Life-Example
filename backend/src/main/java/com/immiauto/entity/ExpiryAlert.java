package com.immiauto.entity;

import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expiry_alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpiryAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expiry_alerts_gen")
    @SequenceGenerator(name = "expiry_alerts_gen", sequenceName = "expiry_alerts_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String alertType; // PASSPORT, STUDY_PERMIT, ECA, LANGUAGE_TEST, PR_CARD, MEDICAL, POLICE_CERT

    @Column(nullable = false)
    private String documentDescription;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column
    private int daysUntilExpiry;

    @Column(nullable = false)
    private String severity; // INFO, WARNING, URGENT, CRITICAL

    @Column
    private boolean acknowledged;

    @Column
    private String acknowledgedBy;

    @Column
    private Long linkedDocumentId;
}
