package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @Generated(event = EventType.INSERT)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String entityType; // Case, Document, Checklist, Reminder

    @Column(nullable = false)
    private UUID entityId;

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
