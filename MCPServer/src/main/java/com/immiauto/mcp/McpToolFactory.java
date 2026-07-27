package com.immiauto.mcp;

import com.immiauto.mcp.config.ToolMappings;
import com.immiauto.mcp.config.ToolMappings.ToolMapping;
import com.immiauto.mcp.http.ApiToolExecutor;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds MCP tool specifications from the JSON-configured {@link ToolMappings}, delegating
 * execution to {@link ApiToolExecutor}. Enforces the per-tool {@code mcp.*} scope and records
 * an audit event for every invocation (forbidden / success / failure).
 */
public final class McpToolFactory {

    /** Baseline scope required when a tool does not declare its own. */
    private static final String DEFAULT_SCOPE = "mcp.tools.read";

    private static final String[] REFERENCE_KEYS =
            {"caseId", "clientId", "caseNumber", "clientNumber", "consultantNumber"};

    private McpToolFactory() {
    }

    public static List<SyncToolSpecification> buildSpecifications(ToolMappings mappings,
                                                                  ApiToolExecutor executor,
                                                                  McpAuditService auditService) {
        List<SyncToolSpecification> specs = new ArrayList<>();
        for (ToolMapping mapping : mappings.tools()) {
            specs.add(toSpecification(mapping, executor, auditService));
        }
        return specs;
    }

    private static SyncToolSpecification toSpecification(ToolMapping mapping,
                                                         ApiToolExecutor executor,
                                                         McpAuditService auditService) {
        Map<String, Object> inputSchema = mapping.inputSchema() == null
                ? Map.of("type", "object", "properties", Map.of())
                : mapping.inputSchema();

        Tool tool = Tool.builder(mapping.name(), inputSchema)
                .description(mapping.description() == null ? "" : mapping.description())
                .build();

        String requiredScope = mapping.requiredScope() == null ? DEFAULT_SCOPE : mapping.requiredScope();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {
                    Map<String, Object> arguments = request.arguments();
                    if (!hasScope(requiredScope)) {
                        audit(auditService, mapping.name(), requiredScope, "FORBIDDEN", arguments);
                        return CallToolResult.builder()
                                .content(List.of(new TextContent(
                                        "Forbidden: this tool requires the '" + requiredScope + "' scope.")))
                                .isError(true)
                                .build();
                    }
                    try {
                        ApiToolExecutor.ApiResult result = executor.execute(mapping, arguments);
                        audit(auditService, mapping.name(), requiredScope,
                                result.successful() ? "SUCCESS" : "FAILURE", arguments);
                        return CallToolResult.builder()
                                .content(List.of(new TextContent(result.asToolText())))
                                .isError(!result.successful())
                                .build();
                    }
                    catch (Exception exception) {
                        if (exception instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        }
                        audit(auditService, mapping.name(), requiredScope, "FAILURE", arguments);
                        return CallToolResult.builder()
                                .content(List.of(new TextContent("Tool execution failed: " + exception.getMessage())))
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }

    /**
     * Checks the current Keycloak token (mapped to {@code SCOPE_*} authorities by McpSecurityConfig)
     * for the required {@code mcp.*} scope.
     *
     * <p>Reads from {@link SecurityContextHolder} — works when the tool handler runs on the HTTP
     * request thread. If the SDK dispatches handlers on a separate (non-child) thread, switch to a
     * transport context-extractor that carries the Authentication into the exchange.
     */
    private static boolean hasScope(String scope) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String required = "SCOPE_" + scope;
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> required.equals(authority.getAuthority()));
    }

    private static void audit(McpAuditService auditService, String toolName, String scope,
                              String status, Map<String, Object> arguments) {
        HttpServletRequest request = currentRequest();
        auditService.record(new McpAuditService.AuditEvent(
                // Keycloak subject identifies the user; there is no Entra oid/tid.
                currentSubject(),
                null,
                toolName,
                scope,
                status,
                inputReference(arguments),
                request == null ? null : request.getRemoteAddr(),
                request == null ? null : request.getHeader("User-Agent")));
    }

    private static String currentSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getSubject();
        }
        return null;
    }

    private static HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }

    /** A non-PII reference to the input (e.g. "caseId=42"); never the raw arguments. */
    private static String inputReference(Map<String, Object> arguments) {
        if (arguments == null) {
            return null;
        }
        for (String key : REFERENCE_KEYS) {
            Object value = arguments.get(key);
            if (value != null) {
                return key + "=" + value;
            }
        }
        return null;
    }
}
