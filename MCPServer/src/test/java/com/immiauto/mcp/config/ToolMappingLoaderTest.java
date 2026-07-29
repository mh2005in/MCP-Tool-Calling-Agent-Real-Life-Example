package com.immiauto.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ToolMappingLoaderTest {

    @TempDir
    Path tempDirectory;

    private final ToolMappingLoader loader = new ToolMappingLoader(new ObjectMapper());

    @Test
    void resolveEnvUsesDefaultWhenVariableUnset() {
        // No such env var -> the default after ':' is used (this is how tools.json points at the
        // backend: ${MCP_BACKEND_API_URL:http://localhost:8080}).
        String resolved = ToolMappingLoader.resolveEnv("\"url\": \"${DEFINITELY_UNSET_VAR_XYZ:http://backend:8080}/api\"");
        assertEquals("\"url\": \"http://backend:8080/api\"", resolved);
    }

    @Test
    void resolveEnvSubstitutesFromEnvironment() {
        // PATH is set in every environment; assert the placeholder is replaced by its value.
        String resolved = ToolMappingLoader.resolveEnv("x=${PATH:fallback}");
        assertNotEquals("x=${PATH:fallback}", resolved);
        assertNotEquals("x=fallback", resolved);
    }

    @Test
    void resolveEnvKeepsLiteralWhenNoValueOrDefault() {
        String resolved = ToolMappingLoader.resolveEnv("${DEFINITELY_UNSET_VAR_XYZ}");
        assertEquals("${DEFINITELY_UNSET_VAR_XYZ}", resolved);
    }

    @Test
    void loadsAValidMapping() throws Exception {
        Path config = tempDirectory.resolve("tools.json");
        Files.writeString(config, """
                {
                  "tools": [{
                    "name": "lookup",
                    "description": "Lookup",
                    "inputSchema": {"type": "object", "properties": {}},
                    "request": {
                      "method": "GET",
                      "url": "https://example.test/items",
                      "timeoutSeconds": 5
                    }
                  }]
                }
                """);

        ToolMappings mappings = loader.load(config);

        assertEquals("lookup", mappings.tools().getFirst().name());
    }

    @Test
    void rejectsDuplicateToolNames() throws Exception {
        Path config = tempDirectory.resolve("tools.json");
        Files.writeString(config, """
                {
                  "tools": [
                    {
                      "name": "lookup",
                      "request": {"method": "GET", "url": "https://example.test/one"}
                    },
                    {
                      "name": "lookup",
                      "request": {"method": "GET", "url": "https://example.test/two"}
                    }
                  ]
                }
                """);

        assertThrows(IllegalArgumentException.class, () -> loader.load(config));
    }

    @Test
    void loadsFromClasspath() throws Exception {
        ToolMappings mappings = loader.loadFromClasspath("config/tools.json");

        assertNotNull(mappings);
        assertFalse(mappings.tools().isEmpty());
    }

    @Test
    void rejectsMissingClasspathResource() {
        assertThrows(IllegalArgumentException.class,
                () -> loader.loadFromClasspath("nonexistent/tools.json"));
    }
}

