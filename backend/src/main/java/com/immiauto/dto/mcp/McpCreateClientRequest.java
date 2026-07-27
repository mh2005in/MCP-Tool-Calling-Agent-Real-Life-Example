package com.immiauto.dto.mcp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class McpCreateClientRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email @NotBlank(message = "Email is required")
    private String email;
}
