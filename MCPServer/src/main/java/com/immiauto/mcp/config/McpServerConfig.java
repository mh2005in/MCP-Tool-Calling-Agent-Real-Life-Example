package com.immiauto.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.mcp.McpAuditService;
import com.immiauto.mcp.McpToolFactory;
import com.immiauto.mcp.http.ApiToolExecutor;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Wires the JSON-configured tools onto an HTTP (Streamable) MCP server endpoint at {@code /mcp}.
 * The transport is a Jakarta Servlet from mcp-core, registered behind Spring Security
 * ({@link McpSecurityConfig} authenticates {@code /mcp/**}).
 *
 * <p>NOTE (verify at build against mcp-core 2.0.0): the transport builder method names below
 * ({@code .jsonMapper(...)}, {@code .mcpEndpoint(...)}) and the provider being a Servlet are the
 * only API points I couldn't confirm remotely — adjust here if they differ. {@code McpServer.sync(...)}
 * / {@code addTool(...)} match the previously-working stdio usage.
 */
@Configuration
public class McpServerConfig {

    private static final String MCP_ENDPOINT = "/mcp";
    private static final String DEFAULT_CLASSPATH_CONFIG = "config/tools.json";

    @Value("${mcp.tool.config:}")
    private String externalToolConfig;

    @Bean
    public ObjectMapper toolObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ApiToolExecutor apiToolExecutor(ObjectMapper toolObjectMapper) {
        // Forward the CALLING USER's token (on-behalf-of) to the backend: the MCP data endpoints
        // resolve the consultant from the authenticated user, so tools must act as that user — not
        // as the MCP service account (which has no linked consultant, and would 500). Audit writes
        // still use the service account (see McpAuditService). The user's JWT is on the request
        // thread's SecurityContext, where the tool handler runs.
        return new ApiToolExecutor(toolObjectMapper, McpServerConfig::currentUserToken);
    }

    /** The raw bearer token of the currently authenticated MCP user, or {@code null} if none. */
    private static String currentUserToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return jwtAuthentication.getToken().getTokenValue();
        }
        return null;
    }

    @Bean
    public ToolMappings toolMappings(ObjectMapper toolObjectMapper) throws IOException {
        ToolMappingLoader loader = new ToolMappingLoader(toolObjectMapper);
        if (externalToolConfig != null && !externalToolConfig.isBlank()) {
            return loader.load(Path.of(externalToolConfig));
        }
        return loader.loadFromClasspath(DEFAULT_CLASSPATH_CONFIG);
    }

    @Bean
    public HttpServletStreamableServerTransportProvider mcpTransportProvider() {
        return HttpServletStreamableServerTransportProvider.builder()
                .jsonMapper(McpJsonDefaults.getMapper())
                .mcpEndpoint(MCP_ENDPOINT)
                .build();
    }

    @Bean
    public ServletRegistrationBean<HttpServletStreamableServerTransportProvider> mcpServletRegistration(
            HttpServletStreamableServerTransportProvider transportProvider) {
        ServletRegistrationBean<HttpServletStreamableServerTransportProvider> registration =
                new ServletRegistrationBean<>(transportProvider, MCP_ENDPOINT, MCP_ENDPOINT + "/*");
        registration.setName("mcpServlet");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    public McpSyncServer mcpSyncServer(HttpServletStreamableServerTransportProvider transportProvider,
                                       ToolMappings toolMappings,
                                       ApiToolExecutor apiToolExecutor,
                                       McpAuditService auditService) {
        McpSyncServer server = McpServer.sync(transportProvider)
                .serverInfo("immigration-mcp-server", "0.1.0")
                .capabilities(ServerCapabilities.builder().tools(false).build())
                .build();

        for (SyncToolSpecification spec :
                McpToolFactory.buildSpecifications(toolMappings, apiToolExecutor, auditService)) {
            server.addTool(spec);
        }
        return server;
    }
}
