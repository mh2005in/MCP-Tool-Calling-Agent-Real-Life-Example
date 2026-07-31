package com.immiauto.service;

import java.util.UUID;

import com.immiauto.dto.mcp.McpAuditEventRequest;
import com.immiauto.entity.McpToolAuditLog;
import com.immiauto.mapper.McpAuditMapper;
import com.immiauto.repository.McpToolAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persists MCP tool-invocation audit events reported by the MCP server.
 */
@Service
@RequiredArgsConstructor
public class McpToolAuditService {

    private final McpToolAuditLogRepository auditLogRepository;
    private final McpAuditMapper auditMapper;

    public void record(McpAuditEventRequest request, UUID consultantId) {
        McpToolAuditLog entry = auditMapper.toEntity(request);
        entry.setConsultantId(consultantId);
        auditLogRepository.save(entry);
    }
}
