package com.immiauto.mapper;

import com.immiauto.dto.mcp.McpAuditEventRequest;
import com.immiauto.entity.McpToolAuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface McpAuditMapper {

    // id is a database-generated UUID (BaseEntity) - not part of the entity builder, so nothing to map.
    @Mapping(target = "consultantId", ignore = true) // set from the MCP API-key consultant
    McpToolAuditLog toEntity(McpAuditEventRequest request);
}
