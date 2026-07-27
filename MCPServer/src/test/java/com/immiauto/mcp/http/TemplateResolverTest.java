package com.immiauto.mcp.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateResolverTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TemplateResolver resolver =
            new TemplateResolver(objectMapper, name -> "TOKEN".equals(name) ? "secret" : null);

    @Test
    void resolvesArgumentsAndEnvironmentVariables() {
        String value = resolver.resolveText(
                "Bearer {{env:TOKEN}} for {{user.name}}",
                Map.of("user", Map.of("name", "Ada")));

        assertEquals("Bearer secret for Ada", value);
    }

    @Test
    void preservesJsonTypesForExactBodyPlaceholders() throws Exception {
        JsonNode template = objectMapper.readTree("""
                {
                  "count": "{{count}}",
                  "label": "Count: {{count}}"
                }
                """);

        JsonNode result = resolver.resolveJson(template, Map.of("count", 7));

        assertTrue(result.get("count").isInt());
        assertEquals(7, result.get("count").intValue());
        assertEquals("Count: 7", result.get("label").textValue());
    }

    @Test
    void urlEncodesPathArguments() {
        assertEquals(
                "https://example.test/A%20B",
                resolver.resolveUrl("https://example.test/{{id}}", Map.of("id", "A B")));
    }

    @Test
    void rejectsMissingArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolveText("{{missing}}", Map.of()));
    }
}

