package com.immiauto.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpiryAlertDto {
    private Long id;
    private Long caseId;
    private String caseNumber;
    private String clientName;
    private String alertType;
    private String documentDescription;
    private LocalDate expiryDate;
    private int daysUntilExpiry;
    private String severity;
    private boolean acknowledged;
    private String acknowledgedBy;
    private Long linkedDocumentId;
    private LocalDateTime createdAt;
}
