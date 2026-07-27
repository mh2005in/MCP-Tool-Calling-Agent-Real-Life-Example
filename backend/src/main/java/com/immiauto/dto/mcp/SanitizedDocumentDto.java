package com.immiauto.dto.mcp;

import com.immiauto.enums.DocumentStatus;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SanitizedDocumentDto {

    private String documentCategory;
    private String documentType;
    private DocumentStatus status;
    private LocalDate expiryDate;
    private String mimeType;
    private boolean translationRequired;
    private boolean notarizationRequired;
}
