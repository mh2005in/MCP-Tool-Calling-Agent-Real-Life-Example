package com.immiauto.mcp.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
 * <p>As in the backend, the issuer (public URL) is validated as-is while signing keys are fetched
 * from a separately configured {@code jwk-set-uri} reachable over the internal Docker network.
 *
 * <p>Public: protected-resource metadata + health. Authenticated: {@code /mcp/**}.
 */
@Configuration
@EnableWebSecurity
public class McpSecurityConfig {

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
                .requestMatchers("/mcp/**").authenticated()
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

    /** Maps the Keycloak {@code scope} claim (space-delimited) to {@code SCOPE_*} authorities. */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
        scopes.setAuthorityPrefix("SCOPE_");
        scopes.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(scopes);
        return converter;
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
