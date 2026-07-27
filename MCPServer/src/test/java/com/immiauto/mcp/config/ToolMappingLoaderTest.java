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

