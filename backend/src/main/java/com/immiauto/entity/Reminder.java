package com.immiauto.entity;

import com.immiauto.enums.ReminderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reminder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ImmigrationCase immigrationCase;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageBody; // AI-drafted, consultant-approved

    @Column(nullable = false)
    private String channel; // email, whatsapp, sms

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderStatus status;

    @Column
    private LocalDateTime scheduledAt;

    @Column
    private LocalDateTime sentAt;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private String approvedBy;

    @Column
    private int attemptCount;
}
