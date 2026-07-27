package com.immiauto.mcp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * OAuth 2.0 Protected Resource Metadata (RFC 9728), required by the MCP Authorization spec
 * (2025-06-18). MCP clients fetch this to discover the authorization server (Entra) and the
 * scopes this resource supports. Kept public (permitted in {@code McpSecurityConfig}).
 */
@RestController
public class OAuthProtectedResourceMetadataController {

    private static final List<String> SCOPES_SUPPORTED = List.of(
            "mcp.tools.read",
            "mcp.cases.read",
            "mcp.documents.read",
            "mcp.checklists.generate",
            "mcp.messages.draft"
    );

    @Value("${app.mcp.resource}")
    private String resource;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @GetMapping({"/.well-known/oauth-protected-resource", "/.well-known/oauth-protected-resource/mcp"})
    public Map<String, Object> metadata() {
        return Map.of(
                "resource", resource,
                "authorization_servers", List.of(issuerUri),
                "scopes_supported", SCOPES_SUPPORTED,
                "bearer_methods_supported", List.of("header")
        );
    }
}
