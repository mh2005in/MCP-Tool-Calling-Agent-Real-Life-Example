package com.immiauto.dto;

import com.immiauto.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistItemDto {
    private Long id;
    private Long caseId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Document name is required")
    private String documentName;

    private String description;
    private DocumentStatus status;
    private boolean required;
    private boolean conditional;
    private String conditionDescription;
    private int sortOrder;
    private String consultantReviewNote;
    private Long linkedDocumentId;
}
