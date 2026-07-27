package com.immiauto.mcp.config;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public record ToolMappings(List<ToolMapping> tools) {

    public record ToolMapping(
            String name,
            String description,
            Map<String, Object> inputSchema,
            String requiredScope,
            HttpRequestMapping request) {
    }

    public record HttpRequestMapping(
            String method,
            String url,
            Map<String, String> headers,
            Map<String, String> query,
            JsonNode body,
            Integer timeoutSeconds) {
    }
}

