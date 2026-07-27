package com.immiauto.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.mcp.config.ToolMappings.HttpRequestMapping;
import com.immiauto.mcp.config.ToolMappings.ToolMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ToolMappingLoader {

    private static final Set<String> SUPPORTED_METHODS =
            Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

    private final ObjectMapper objectMapper;

    public ToolMappingLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ToolMappings load(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Tool mapping file does not exist: " + path.toAbsolutePath());
        }

        ToolMappings mappings = objectMapper.readValue(path.toFile(), ToolMappings.class);
        validate(mappings);
        return mappings;
    }

    public ToolMappings loadFromClasspath(String resourcePath) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException(
                        "Classpath resource not found: " + resourcePath);
            }
            ToolMappings mappings = objectMapper.readValue(in, ToolMappings.class);
            validate(mappings);
            return mappings;
        }
    }

    private void validate(ToolMappings mappings) {
        if (mappings == null || mappings.tools() == null || mappings.tools().isEmpty()) {
            throw new IllegalArgumentException("At least one tool mapping is required");
        }

        Set<String> names = new HashSet<>();
        for (ToolMapping tool : mappings.tools()) {
            requireText(tool.name(), "Tool name is required");
            if (!names.add(tool.name())) {
                throw new IllegalArgumentException("Duplicate tool name: " + tool.name());
            }
            if (tool.request() == null) {
                throw new IllegalArgumentException("HTTP request mapping is required for tool: " + tool.name());
            }
            validateRequest(tool.name(), tool.request());
        }
    }

    private void validateRequest(String toolName, HttpRequestMapping request) {
        requireText(request.method(), "HTTP method is required for tool: " + toolName);
        String method = request.method().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_METHODS.contains(method)) {
            throw new IllegalArgumentException("Unsupported HTTP method for tool " + toolName + ": " + method);
        }

        requireText(request.url(), "HTTP URL is required for tool: " + toolName);
        if (!request.url().startsWith("http://") && !request.url().startsWith("https://")) {
            throw new IllegalArgumentException("Tool URL must use http or https: " + toolName);
        }

        if (request.timeoutSeconds() != null && request.timeoutSeconds() <= 0) {
            throw new IllegalArgumentException("timeoutSeconds must be positive for tool: " + toolName);
        }

        rejectNullEntries(request.headers(), "header", toolName);
        rejectNullEntries(request.query(), "query parameter", toolName);
    }

    private void rejectNullEntries(Map<String, String> values, String label, String toolName) {
        if (values != null && values.entrySet().stream()
                .anyMatch(entry -> entry.getKey() == null || entry.getValue() == null)) {
            throw new IllegalArgumentException("Null " + label + " in tool: " + toolName);
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}

