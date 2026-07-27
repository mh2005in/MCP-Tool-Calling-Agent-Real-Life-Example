package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_logs_gen")
    @SequenceGenerator(name = "audit_logs_gen", sequenceName = "audit_logs_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String entityType; // Case, Document, Checklist, Reminder

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String action; // CREATED, UPDATED, STATUS_CHANGED, APPROVED, etc.

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @PrePersist
    public void prePersist() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }
}
