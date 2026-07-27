package com.immiauto.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Posts MCP tool-invocation audit events to the backend ({@code POST /v1/mcp/audit}) using a
 * Keycloak service-account (client-credentials) token. Best-effort: audit failures never break a
 * tool call.
 */
@Service
public class McpAuditService {

    private static final Logger log = LoggerFactory.getLogger(McpAuditService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final BackendServiceTokenService backendServiceTokenService;

    @Value("${mcp.backend.audit-url}")
    private String auditUrl;

    public McpAuditService(BackendServiceTokenService backendServiceTokenService) {
        this.backendServiceTokenService = backendServiceTokenService;
    }

    public void record(AuditEvent event) {
        try {
            String bearerToken = backendServiceTokenService.backendToken();
            if (bearerToken == null) {
                log.warn("Skipping MCP audit POST: no backend service token available");
                return;
            }
            String json = objectMapper.writeValueAsString(event);
            HttpRequest request = HttpRequest.newBuilder(URI.create(auditUrl))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + bearerToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() >= 300) {
                log.warn("MCP audit POST returned HTTP {}", response.statusCode());
            }
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("MCP audit POST interrupted", ex);
        }
        catch (Exception ex) {
            log.warn("MCP audit POST failed: {}", ex.getMessage());
        }
    }

    /** Mirrors the backend {@code McpAuditEventRequest} field names for JSON binding. */
    public record AuditEvent(
            String externalSubject,
            String tenantId,
            String toolName,
            String requiredScope,
            String resultStatus,
            String inputReference,
            String ipAddress,
            String userAgent) {
    }
}
