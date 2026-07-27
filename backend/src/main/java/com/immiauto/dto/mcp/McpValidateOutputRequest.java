package com.immiauto.dto.mcp;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class McpValidateOutputRequest {

    @NotBlank(message = "Text is required")
    private String text;
}
