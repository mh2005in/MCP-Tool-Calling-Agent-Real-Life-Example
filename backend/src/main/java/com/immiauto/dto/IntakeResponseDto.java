package com.immiauto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeResponseDto {
    private Long id;
    private Long caseId;
    private String sectionName;
    @NotBlank(message = "Question key is required")
    private String questionKey;
    private String questionLabel;
    private String answer;
    private int sortOrder;
    private boolean flaggedForReview;
}
