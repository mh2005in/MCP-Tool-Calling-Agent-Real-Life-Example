package com.immiauto.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The controller advice must map an authorization denial to 403, not let it fall through to the
 * catch-all 500. Uses a standalone MockMvc with a throwing controller and only the advice wired in
 * (no security/DB), so it exercises the exact exception-resolution path.
 */
class GlobalExceptionHandlerTest {

    @RestController
    static class ThrowingController {
        // Mirrors a @PreAuthorize guard returning false, which Spring Security 6 surfaces as an
        // AccessDeniedException (AuthorizationDeniedException) into the dispatcher.
        @GetMapping("/boom-access-denied")
        String accessDenied() {
            throw new AccessDeniedException("nope");
        }

        // An unmatched URL resolves to the static-resource handler, which throws this; the advice
        // must render it as 404, not the catch-all 500.
        @GetMapping("/boom-no-resource")
        String noResource() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/v1/consultants");
        }

        @GetMapping("/boom-unexpected")
        String unexpected() {
            throw new RuntimeException("kaboom");
        }
    }

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ThrowingController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void accessDeniedIsForbiddenNotServerError() throws Exception {
        // Fails (500) without the AccessDeniedException handler; passes (403) with it.
        mockMvc.perform(get("/boom-access-denied"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unmatchedRouteIsNotFoundNotServerError() throws Exception {
        // Fails (500) without the NoResourceFoundException handler; passes (404) with it.
        mockMvc.perform(get("/boom-no-resource"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unexpectedExceptionStillMapsToServerError() throws Exception {
        mockMvc.perform(get("/boom-unexpected"))
                .andExpect(status().isInternalServerError());
    }
}
