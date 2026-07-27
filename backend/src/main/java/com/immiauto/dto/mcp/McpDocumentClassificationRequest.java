package com.immiauto.dto.mcp;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class McpDocumentClassificationRequest {

    @NotBlank(message = "Filename is required")
    private String filename;

    private String mimeType;
    private Long size;
}
