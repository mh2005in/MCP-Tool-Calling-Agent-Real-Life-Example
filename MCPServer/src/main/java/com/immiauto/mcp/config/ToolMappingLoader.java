package com.immiauto.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.mcp.config.ToolMappings.HttpRequestMapping;
import com.immiauto.mcp.config.ToolMappings.ToolMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ToolMappingLoader {

    private static final Set<String> SUPPORTED_METHODS =
            Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

    /** Matches {@code ${NAME}} or {@code ${NAME:default}} placeholders, mirroring Spring's syntax. */
    private static final Pattern ENV_PLACEHOLDER = Pattern.compile("\\$\\{([A-Za-z0-9_]+)(?::([^}]*))?}");

    private final ObjectMapper objectMapper;

    public ToolMappingLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ToolMappings load(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Tool mapping file does not exist: " + path.toAbsolutePath());
        }

        ToolMappings mappings = parse(Files.readString(path));
        validate(mappings);
        return mappings;
    }

    public ToolMappings loadFromClasspath(String resourcePath) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException(
                        "Classpath resource not found: " + resourcePath);
            }
            ToolMappings mappings = parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            validate(mappings);
            return mappings;
        }
    }

    private ToolMappings parse(String json) throws IOException {
        return objectMapper.readValue(resolveEnv(json), ToolMappings.class);
    }

    /**
     * Substitutes {@code ${ENV:default}} placeholders in the raw config from environment variables
     * before parsing, so hosts/URLs stay env-driven (e.g. the backend base URL differs between a
     * local {@code mvn} run and Docker). Uses the default when the variable is unset; keeps the
     * literal placeholder when there is neither a value nor a default (so validation flags it).
     */
    static String resolveEnv(String raw) {
        Matcher matcher = ENV_PLACEHOLDER.matcher(raw);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            String fallback = matcher.group(2);
            String value = System.getenv(name);
            if (value == null) {
                value = fallback != null ? fallback : matcher.group();
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(out);
        return out.toString();
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

