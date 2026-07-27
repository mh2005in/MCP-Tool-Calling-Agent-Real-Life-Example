package com.immiauto.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts a Microsoft Entra token into Spring Security authorities.
 *
 * <p>App roles (claim {@code roles}) become {@code ROLE_*} authorities. Delegated OAuth
 * scopes are not used: this is a single first-party consultant portal, so authorization is
 * row-level/DB-driven (see {@link ConsultantAccessService}, {@link AdminAccessService}),
 * not scope-driven. Scope mapping can be reintroduced for Phase 2/3 (MCP/OBO) if needed.
 */
@Component
public class JwtAuthoritiesConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLES_CLAIM = "roles";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Collection<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);
        if (roles != null) {
            for (String role : roles) {
                if (role != null && !role.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
