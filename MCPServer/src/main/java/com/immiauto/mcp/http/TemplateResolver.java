package com.immiauto.mcp.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateResolver {

    private static final Pattern PLACEHOLDER =
            Pattern.compile("\\{\\{\\s*(env:)?([A-Za-z_][A-Za-z0-9_.-]*)\\s*}}");

    private final ObjectMapper objectMapper;
    private final Function<String, String> environmentLookup;

    public TemplateResolver(ObjectMapper objectMapper) {
        this(objectMapper, System::getenv);
    }

    TemplateResolver(ObjectMapper objectMapper, Function<String, String> environmentLookup) {
        this.objectMapper = objectMapper;
        this.environmentLookup = environmentLookup;
    }

    public String resolveText(String template, Map<String, Object> arguments) {
        return resolveText(template, arguments, false);
    }

    public String resolveUrl(String template, Map<String, Object> arguments) {
        return resolveText(template, arguments, true);
    }

    public JsonNode resolveJson(JsonNode template, Map<String, Object> arguments) {
        if (template == null || template.isNull()) {
            return null;
        }
        if (template.isObject()) {
            ObjectNode result = objectMapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = template.fields();
            fields.forEachRemaining(entry -> result.set(entry.getKey(), resolveJson(entry.getValue(), arguments)));
            return result;
        }
        if (template.isArray()) {
            ArrayNode result = objectMapper.createArrayNode();
            template.forEach(value -> result.add(resolveJson(value, arguments)));
            return result;
        }
        if (!template.isTextual()) {
            return template.deepCopy();
        }

        Matcher matcher = PLACEHOLDER.matcher(template.textValue());
        if (matcher.matches() && matcher.group(1) == null) {
            Object value = argument(arguments, matcher.group(2));
            return objectMapper.valueToTree(value);
        }
        return TextNode.valueOf(resolveText(template.textValue(), arguments));
    }

    private String resolveText(String template, Map<String, Object> arguments, boolean encodeArguments) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            boolean environmentValue = matcher.group(1) != null;
            String key = matcher.group(2);
            Object value = environmentValue ? environment(key) : argument(arguments, key);
            String replacement = stringify(value);
            if (encodeArguments && !environmentValue) {
                replacement = urlEncode(replacement);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private Object argument(Map<String, Object> arguments, String path) {
        Object current = arguments;
        for (String part : path.split("\\.")) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(part)) {
                throw new IllegalArgumentException("Missing required template argument: " + path);
            }
            current = map.get(part);
        }
        if (current == null) {
            throw new IllegalArgumentException("Template argument is null: " + path);
        }
        return current;
    }

    private String environment(String name) {
        String value = environmentLookup.apply(name);
        if (value == null) {
            throw new IllegalArgumentException("Missing environment variable: " + name);
        }
        return value;
    }

    private String stringify(Object value) {
        if (value instanceof String string) {
            return string;
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        try {
            return objectMapper.writeValueAsString(value);
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Cannot convert template value to text", exception);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}

