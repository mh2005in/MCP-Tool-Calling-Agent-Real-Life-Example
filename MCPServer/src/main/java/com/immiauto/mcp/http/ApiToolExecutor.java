package com.immiauto.mcp.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.mcp.config.ToolMappings.HttpRequestMapping;
import com.immiauto.mcp.config.ToolMappings.ToolMapping;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class ApiToolExecutor {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final TemplateResolver templateResolver;
    /** Supplies the backend bearer token (OBO) for the current call; returns null to send no Authorization. */
    private final Supplier<String> bearerTokenSupplier;

    public ApiToolExecutor(ObjectMapper objectMapper) {
        this(objectMapper, () -> null);
    }

    public ApiToolExecutor(ObjectMapper objectMapper, Supplier<String> bearerTokenSupplier) {
        this(
                objectMapper,
                HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build(),
                new TemplateResolver(objectMapper),
                bearerTokenSupplier);
    }

    ApiToolExecutor(ObjectMapper objectMapper, HttpClient httpClient, TemplateResolver templateResolver) {
        this(objectMapper, httpClient, templateResolver, () -> null);
    }

    ApiToolExecutor(ObjectMapper objectMapper, HttpClient httpClient, TemplateResolver templateResolver,
                    Supplier<String> bearerTokenSupplier) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.templateResolver = templateResolver;
        this.bearerTokenSupplier = bearerTokenSupplier;
    }

    public ApiResult execute(ToolMapping tool, Map<String, Object> arguments)
            throws IOException, InterruptedException {
        HttpRequestMapping mapping = tool.request();
        String method = mapping.method().toUpperCase(Locale.ROOT);
        String url = appendQuery(
                templateResolver.resolveUrl(mapping.url(), arguments),
                mapping.query(),
                arguments);

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(mapping.timeoutSeconds() == null ? 30 : mapping.timeoutSeconds()));

        if (mapping.headers() != null) {
            mapping.headers().forEach((name, value) ->
                    request.header(name, templateResolver.resolveText(value, arguments)));
        }

        String bearerToken = bearerTokenSupplier.get();
        if (bearerToken != null && !bearerToken.isBlank()) {
            request.header("Authorization", "Bearer " + bearerToken);
        }

        JsonNode resolvedBody = templateResolver.resolveJson(mapping.body(), arguments);
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
        if (resolvedBody != null) {
            publisher = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(resolvedBody));
            if (mapping.headers() == null
                    || mapping.headers().keySet().stream().noneMatch("Content-Type"::equalsIgnoreCase)) {
                request.header("Content-Type", "application/json");
            }
        }

        request.method(method, publisher);
        HttpResponse<String> response =
                httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new ApiResult(response.statusCode(), response.body());
    }

    private String appendQuery(
            String url,
            Map<String, String> query,
            Map<String, Object> arguments) {
        if (query == null || query.isEmpty()) {
            return url;
        }

        StringBuilder result = new StringBuilder(url);
        result.append(url.contains("?") ? '&' : '?');
        boolean first = true;
        for (Map.Entry<String, String> entry : query.entrySet()) {
            if (!first) {
                result.append('&');
            }
            first = false;
            result.append(urlEncode(entry.getKey()))
                    .append('=')
                    .append(urlEncode(templateResolver.resolveText(entry.getValue(), arguments)));
        }
        return result.toString();
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public record ApiResult(int statusCode, String body) {

        public boolean successful() {
            return statusCode >= 200 && statusCode < 300;
        }

        public String asToolText() {
            return "HTTP " + statusCode + "\n" + (body == null ? "" : body);
        }
    }
}

