package com.immiauto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Audit record of an MCP tool invocation. Stores who/which-tenant/which-tool/scope/outcome
 * plus request metadata and a non-PII input reference. Written from the MCP server via
 * {@code POST /v1/mcp/audit}.
 */
@Entity
@Table(name = "mcp_tool_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McpToolAuditLog extends BaseEntity {

    /** Entra subject (oid) of the user who invoked the tool. */
    @Column(name = "external_subject")
    private String externalSubject;

    @Column(name = "tenant_id")
    private String tenantId;

    /** Consultant resolved from the MCP API key that made the downstream call. */
    @Column(name = "consultant_id")
    private UUID consultantId;

    @Column(name = "tool_name", nullable = false)
    private String toolName;

    @Column(name = "required_scope")
    private String requiredScope;

    @Column(name = "result_status", nullable = false)
    private String resultStatus;

    @Column(name = "input_reference")
    private String inputReference;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;
}
