package com.immiauto.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Obtains a Keycloak service-account token (OAuth2 client-credentials grant) for the MCP client,
 * used to authenticate server-to-server audit writes to the backend ({@code POST /v1/mcp/audit}).
 *
 * <p>Replaces the previous Microsoft Entra On-Behalf-Of exchange. The MCP client is configured
 * with an audience mapper so its tokens carry the backend audience ({@code immiauto-backend}),
 * satisfying the backend's audience validation. The token is cached until shortly before expiry.
 *
 * <p>Note: unlike the former OBO flow, this token identifies the MCP <i>service account</i>, not the
 * calling user. The real user identity is still recorded in the audit event body (see
 * {@link McpToolFactory}); only the transport authentication uses the service account.
 */
@Service
public class BackendServiceTokenService {

    private static final Logger log = LoggerFactory.getLogger(BackendServiceTokenService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final AtomicReference<CachedToken> cache = new AtomicReference<>();

    @Value("${mcp.backend.token-endpoint}")
    private String tokenEndpoint;

    @Value("${mcp.backend.client-id}")
    private String clientId;

    @Value("${mcp.backend.client-secret:}")
    private String clientSecret;

    /** @return a backend-audience service-account access token, or null if it could not be obtained. */
    public String backendToken() {
        CachedToken cached = cache.get();
        if (cached != null && cached.isFresh()) {
            return cached.token();
        }
        CachedToken fetched = requestToken();
        if (fetched != null) {
            cache.set(fetched);
            return fetched.token();
        }
        return null;
    }

    private CachedToken requestToken() {
        try {
            String form = "grant_type=client_credentials"
                    + "&client_id=" + enc(clientId)
                    + "&client_secret=" + enc(clientSecret);

            HttpRequest request = HttpRequest.newBuilder(URI.create(tokenEndpoint))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300) {
                log.warn("Service-account token request failed (HTTP {}): {}", response.statusCode(), response.body());
                return null;
            }
            JsonNode body = objectMapper.readTree(response.body());
            String accessToken = body.path("access_token").asText(null);
            long expiresIn = body.path("expires_in").asLong(300);
            if (accessToken == null) {
                return null;
            }
            // Refresh 60s before actual expiry.
            return new CachedToken(accessToken, Instant.now().plusSeconds(Math.max(expiresIn - 60, 30)));
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Service-account token request interrupted", ex);
            return null;
        }
        catch (Exception ex) {
            log.warn("Service-account token request error: {}", ex.getMessage());
            return null;
        }
    }

    private static String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private record CachedToken(String token, Instant expiresAt) {
        boolean isFresh() {
            return Instant.now().isBefore(expiresAt);
        }
    }
}
