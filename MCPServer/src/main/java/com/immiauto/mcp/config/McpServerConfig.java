package com.immiauto.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.mcp.BackendServiceTokenService;
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
    public ApiToolExecutor apiToolExecutor(ObjectMapper toolObjectMapper,
                                           BackendServiceTokenService backendServiceTokenService) {
        // Attach a Keycloak service-account backend token to every tool's downstream call.
        return new ApiToolExecutor(toolObjectMapper, backendServiceTokenService::backendToken);
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
