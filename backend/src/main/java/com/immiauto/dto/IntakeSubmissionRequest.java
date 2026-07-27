package com.immiauto.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeSubmissionRequest {
    @NotEmpty(message = "Responses cannot be empty")
    private List<IntakeResponseDto> responses;
}
