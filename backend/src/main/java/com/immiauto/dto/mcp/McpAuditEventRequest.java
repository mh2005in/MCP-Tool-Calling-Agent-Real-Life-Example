package com.immiauto.dto.mcp;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Audit event posted by the MCP server for each tool invocation. Carries references only
 * (no full prompts/responses) per the MCP audit guidance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McpAuditEventRequest {

    /** Entra subject (oid) of the user who invoked the tool. */
    private String externalSubject;

    /** Entra tenant id (tid). */
    private String tenantId;

    @NotBlank(message = "toolName is required")
    private String toolName;

    /** Scope required by the tool (e.g. mcp.cases.read). */
    private String requiredScope;

    /** SUCCESS / FAILURE / FORBIDDEN. */
    @NotBlank(message = "resultStatus is required")
    private String resultStatus;

    /** Compact reference to the input (e.g. "caseId=42"), never raw PII. */
    private String inputReference;

    private String ipAddress;

    private String userAgent;
}
