package com.immiauto.dto.mcp;

import com.immiauto.enums.ReminderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class McpReminderDto {

    private String subject;
    private String channel;
    private ReminderStatus status;
    private LocalDateTime scheduledAt;
}
