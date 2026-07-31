package com.immiauto.entity;

import com.immiauto.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expiry_alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpiryAlert extends BaseEntity {

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
    private UUID linkedDocumentId;
}
