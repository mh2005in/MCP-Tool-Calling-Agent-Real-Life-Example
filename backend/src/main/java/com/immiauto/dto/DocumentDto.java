package com.immiauto.dto;

import com.immiauto.enums.DocumentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentDto {
    private Long id;
    private Long caseId;
    private String originalFileName;
    private String mimeType;
    private Long fileSizeBytes;
    private String documentCategory;
    private String documentType;
    private DocumentStatus status;
    private LocalDate expiryDate;
    private String reviewNote;
    private String rejectionReason;
    private boolean translationRequired;
    private boolean notarizationRequired;
    private String standardizedName;
    private LocalDateTime createdAt;
}
