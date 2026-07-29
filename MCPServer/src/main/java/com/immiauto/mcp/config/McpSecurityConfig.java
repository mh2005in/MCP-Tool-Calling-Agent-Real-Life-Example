package com.immiauto.mcp.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The MCP server is a Keycloak (OpenID Connect) OAuth2 resource server. Unlike the backend portal,
 * it DOES use scopes: the {@code mcp.*} scopes are enforced per tool, so the Keycloak {@code scope}
 * claim (space-delimited) is mapped to {@code SCOPE_*} authorities here.
 *
 * <p>Client apps may self-register via Keycloak Dynamic Client Registration (RFC 7591), so we can't
 * rely on a pre-provisioned, trusted client to gate access. Authorization instead hinges on the
 * <em>user</em>: the Keycloak realm {@code roles} claim is mapped to {@code ROLE_*} authorities and
 * {@code /mcp/**} requires one of the app's roles ({@link #APP_ROLES}). A token minted through a
 * dynamically-registered client whose subject is not a real app user (no such role) is rejected with
 * 403 — anonymous DCR opens client registration, not access.
 *
 * <p>As in the backend, the issuer (public URL) is validated as-is while signing keys are fetched
 * from a separately configured {@code jwk-set-uri} reachable over the internal Docker network.
 *
 * <p>Public: protected-resource metadata + health. Authorized app users only: {@code /mcp/**}.
 */
@Configuration
@EnableWebSecurity
public class McpSecurityConfig {

    /** Realm roles that identify a legitimate app user; everyone else is denied {@code /mcp/**}. */
    static final String[] APP_ROLES = {"CONSULTANT_OWNER", "ADMIN"};

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.security.audience}")
    private String audience;

    @Value("${app.security.audience-validation.enabled:true}")
    private boolean audienceValidationEnabled;

    @Bean
    public SecurityFilterChain mcpSecurity(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/.well-known/**", "/actuator/health").permitAll()
                .requestMatchers("/mcp/**").hasAnyRole(APP_ROLES)
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(JwtValidators.createDefaultWithIssuer(issuerUri));
        if (audienceValidationEnabled) {
            validators.add(new AudienceValidator(audience));
        }
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }

    /**
     * Maps two sources of authority: the Keycloak {@code scope} claim (space-delimited) to
     * {@code SCOPE_*} for per-tool scope checks, and the standard {@code realm_access.roles} claim
     * to {@code ROLE_*} so {@code /mcp/**} can require an app role (see {@link #APP_ROLES}). Combining
     * both lets us open Dynamic Client Registration while still gating access on a known app user.
     *
     * <p>{@code realm_access.roles} is emitted by Keycloak's built-in {@code roles} client scope,
     * which every client — including dynamically-registered ones — carries by default, so no custom
     * mapper is needed on registered clients.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
        scopes.setAuthorityPrefix("SCOPE_");
        scopes.setAuthoritiesClaimName("scope");

        Converter<Jwt, Collection<GrantedAuthority>> authorities = jwt -> {
            List<GrantedAuthority> merged = new ArrayList<>(scopes.convert(jwt));
            for (String role : realmRoles(jwt)) {
                merged.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return merged;
        };

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    /** Reads realm role names from the Keycloak {@code realm_access.roles} claim ({@code []} if absent). */
    @SuppressWarnings("unchecked")
    private static List<String> realmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
            return (List<String>) roles;
        }
        return List.of();
    }

    private record AudienceValidator(String expectedAudiences) implements OAuth2TokenValidator<Jwt> {

        private static final OAuth2Error ERROR =
                new OAuth2Error("invalid_token", "The required audience is missing", null);

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            List<String> tokenAudiences = jwt.getClaimAsStringList(JwtClaimNames.AUD);
            if (tokenAudiences != null) {
                for (String accepted : expectedAudiences.split(",")) {
                    if (tokenAudiences.contains(accepted.trim())) {
                        return OAuth2TokenValidatorResult.success();
                    }
                }
            }
            return OAuth2TokenValidatorResult.failure(ERROR);
        }
    }
}
