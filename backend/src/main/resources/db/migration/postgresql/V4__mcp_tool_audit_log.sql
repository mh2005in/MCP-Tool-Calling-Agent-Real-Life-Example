-- ============================================================
-- Immigration Automation - Database Schema (PostgreSQL)
-- V4: MCP tool invocation audit log
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS immiauto_db.mcp_tool_audit_log_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE immiauto_db.mcp_tool_audit_log (
    id                BIGINT       NOT NULL DEFAULT nextval('immiauto_db.mcp_tool_audit_log_seq'),
    external_subject  VARCHAR(255),
    tenant_id         VARCHAR(255),
    consultant_id     BIGINT,
    tool_name         VARCHAR(255) NOT NULL,
    required_scope    VARCHAR(255),
    result_status     VARCHAR(255) NOT NULL,
    input_reference   VARCHAR(255),
    ip_address        VARCHAR(255),
    user_agent        VARCHAR(255),
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_mcp_tool_audit_log PRIMARY KEY (id),
    CONSTRAINT fk_mcp_tool_audit_log_consultant FOREIGN KEY (consultant_id) REFERENCES immiauto_db.consultants (id)
);
