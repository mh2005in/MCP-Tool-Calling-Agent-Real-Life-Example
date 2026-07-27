package com.immiauto.mapper;

import com.immiauto.dto.mcp.McpAuditEventRequest;
import com.immiauto.entity.McpToolAuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface McpAuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consultantId", ignore = true) // set from the MCP API-key consultant
    McpToolAuditLog toEntity(McpAuditEventRequest request);
}
