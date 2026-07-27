package com.immiauto.security;

import com.immiauto.entity.AppUser;
import com.immiauto.service.UserProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Resolves the {@link AppUser} backing the current Entra access token from the security context.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserProvisioningService userProvisioningService;

    /** @return the application user for the current request, provisioning on first sign-in. */
    public AppUser getCurrentUser() {
        return userProvisioningService.resolve(getCurrentJwt());
    }

    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("No authenticated Entra token in the security context");
    }
}
