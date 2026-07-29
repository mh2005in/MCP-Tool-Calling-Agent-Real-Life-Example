package com.immiauto.mcp.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Verifies the authority mapping that gates {@code /mcp/**}. Because client apps may self-register
 * via Dynamic Client Registration, access must hinge on the user's realm roles rather than a trusted
 * client: a token whose subject lacks an app role ({@link McpSecurityConfig#APP_ROLES}) yields no
 * {@code ROLE_*} authority and is therefore denied by {@code hasAnyRole(...)}.
 */
class McpSecurityConfigTest {

    private final JwtAuthenticationConverter converter =
            new McpSecurityConfig().jwtAuthenticationConverter();

    @Test
    void mapsRealmRolesAndScopesToAuthorities() {
        Jwt jwt = jwt(Map.of(
                "scope", "mcp.tools.read mcp.cases.read",
                "realm_access", Map.of("roles", List.of("CONSULTANT_OWNER", "offline_access"))));

        assertThat(authorities(jwt)).contains(
                "SCOPE_mcp.tools.read",
                "SCOPE_mcp.cases.read",
                "ROLE_CONSULTANT_OWNER",
                "ROLE_offline_access");
    }

    @Test
    void grantsNoAppRoleWhenUserIsNotAnAppUser() {
        // A token minted through a dynamically-registered client whose subject has no app role.
        Jwt jwt = jwt(Map.of(
                "scope", "mcp.tools.read",
                "realm_access", Map.of("roles", List.of("default-roles-immiauto"))));

        Collection<String> granted = authorities(jwt);
        assertThat(granted).doesNotContain("ROLE_CONSULTANT_OWNER", "ROLE_ADMIN");
        assertThat(granted).contains("SCOPE_mcp.tools.read");
    }

    @Test
    void toleratesMissingRealmAccessClaim() {
        Jwt jwt = jwt(Map.of("scope", "mcp.tools.read"));

        assertThat(authorities(jwt)).containsExactly("SCOPE_mcp.tools.read");
    }

    private Collection<String> authorities(Jwt jwt) {
        AbstractAuthenticationToken token = converter.convert(jwt);
        return token.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }

    private static Jwt jwt(Map<String, Object> claims) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300));
        claims.forEach(builder::claim);
        return builder.build();
    }
}
