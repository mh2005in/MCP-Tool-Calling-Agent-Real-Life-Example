package com.immiauto.dto.mcp;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MaskedIntakeResponseDto {

    private String sectionName;
    private String questionKey;
    private String questionLabel;
    private String answer;
    private int sortOrder;
    private boolean flaggedForReview;
}
