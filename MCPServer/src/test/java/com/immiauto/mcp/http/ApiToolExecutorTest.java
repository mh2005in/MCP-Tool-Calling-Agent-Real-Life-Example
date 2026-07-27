package com.immiauto.mcp.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immiauto.mcp.config.ToolMappings.HttpRequestMapping;
import com.immiauto.mcp.config.ToolMappings.ToolMapping;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiToolExecutorTest {

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void invokesConfiguredApiWithResolvedBody() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/clients/42", exchange -> {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            byte[] response = requestBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(201, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        ObjectMapper objectMapper = new ObjectMapper();
        ToolMapping mapping = new ToolMapping(
                "update_client",
                "Update a client",
                Map.of("type", "object"),
                "mcp.tools.read",
                new HttpRequestMapping(
                        "PUT",
                        "http://localhost:" + server.getAddress().getPort() + "/clients/{{id}}",
                        Map.of(),
                        Map.of(),
                        objectMapper.readTree("{\"active\":\"{{active}}\"}"),
                        5));

        ApiToolExecutor.ApiResult result =
                new ApiToolExecutor(objectMapper).execute(mapping, Map.of("id", 42, "active", true));

        assertEquals(201, result.statusCode());
        assertTrue(result.successful());
        assertEquals("{\"active\":true}", result.body());
    }
}

