package com.immiauto.security;

import com.immiauto.entity.AppUser;
import com.immiauto.enums.AppUserStatus;
import com.immiauto.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Blocks DISABLED users on every authenticated request, so disabling a consultant takes effect
 * immediately even while they still hold an unexpired token (Entra can't revoke the JWT mid-flight).
 *
 * <p>{@code GET /v1/me} is allowed through so the frontend can read the DISABLED status and route to
 * the unauthorized page cleanly. Lookups are by external subject (no provisioning side effects here).
 */
@Component
@RequiredArgsConstructor
public class DisabledUserFilter extends OncePerRequestFilter {

    private final AppUserRepository appUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwt && !isMeEndpoint(request)) {
            String subject = jwt.getToken().getClaimAsString("oid");
            if (subject == null || subject.isBlank()) {
                subject = jwt.getToken().getSubject();
            }
            AppUser user = appUserRepository.findByExternalSubject(subject).orElse(null);
            if (user != null && user.getStatus() == AppUserStatus.DISABLED) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Account disabled\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isMeEndpoint(HttpServletRequest request) {
        // servlet path excludes the /api context path
        return "/v1/me".equals(request.getServletPath());
    }
}
