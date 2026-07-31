package com.immiauto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeResponseDto {
    private UUID id;
    private UUID caseId;
    private String sectionName;
    @NotBlank(message = "Question key is required")
    private String questionKey;
    private String questionLabel;
    private String answer;
    private int sortOrder;
    private boolean flaggedForReview;
}
