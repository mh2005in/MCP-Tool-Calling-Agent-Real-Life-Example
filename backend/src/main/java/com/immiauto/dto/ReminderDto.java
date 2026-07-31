package com.immiauto.dto;

import com.immiauto.enums.ReminderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderDto {
    private UUID id;
    private UUID caseId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message body is required")
    private String messageBody;

    private String channel;
    private ReminderStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private int attemptCount;
}
