package com.immiauto.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * HTTP MCP server, secured as a Microsoft Entra OAuth2 resource server.
 * Replaces the former stdio entry point ({@code ApiMappingMcpServer}); this server is HTTP-only.
 */
@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        // Let tool-call handlers spawned from the HTTP request thread inherit the security context
        // (used by per-tool scope checks in McpToolFactory).
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SpringApplication.run(McpServerApplication.class, args);
    }
}
