package com.immiauto.config;

import com.immiauto.security.DisabledUserFilter;
import com.immiauto.security.JwtAuthoritiesConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

/**
 * Security configuration: the backend is a Keycloak (OpenID Connect) OAuth2 Resource Server.
 *
 * <p>Audience validation is gated behind {@code app.security.audience-validation.enabled}.
 *
 * <p>The token's issuer (a public URL, e.g. {@code http://localhost:8085/realms/immiauto}) is
 * validated as-is, but signing keys are fetched from a separately configured {@code jwk-set-uri}.
 * This decoupling lets the container reach Keycloak over the internal Docker network
 * ({@code http://keycloak:8080/...}) while the browser and the token's {@code iss} use the public
 * host URL, avoiding the classic localhost-vs-service-name issuer mismatch.
 *
 * <p>{@code /v1/mcp/**} is authenticated like the rest of the API: the MCP server calls it with a
 * Keycloak service-account token so audit writes are authenticated.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthoritiesConverter jwtAuthoritiesConverter;
    private final DisabledUserFilter disabledUserFilter;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.security.audience}")
    private String audience;

    @Value("${app.security.audience-validation.enabled:false}")
    private boolean audienceValidationEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public infrastructure / docs
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                // Public client-facing view (accessed via shared link, no login)
                .requestMatchers("/v1/checklist/client/**").permitAll()
                // Everything else (incl. /v1/mcp/** via MCP On-Behalf-Of tokens) requires a valid Entra token
                .requestMatchers("/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthoritiesConverter)
                )
            )
            // Block DISABLED consultants on every request once authenticated.
            .addFilterAfter(disabledUserFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Keys are fetched from jwk-set-uri (reachable over the internal Docker network), while the
        // issuer is validated against its public URL. Key fetching is lazy, so the backend can start
        // before Keycloak is ready.
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(JwtValidators.createDefaultWithIssuer(issuerUri));
        if (audienceValidationEnabled) {
            validators.add(new AudienceValidator(audience));
        }
        decoder.setJwtValidator(new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }

    /**
     * Validates that the token's {@code aud} claim contains one of the accepted backend API
     * audiences (comma-separated config; e.g. the App ID URI and/or the bare client-id GUID).
     */
    private record AudienceValidator(String expectedAudiences)
            implements OAuth2TokenValidator<Jwt> {

        private static final org.springframework.security.oauth2.core.OAuth2Error ERROR =
                new org.springframework.security.oauth2.core.OAuth2Error(
                        "invalid_token", "The required audience is missing", null);

        @Override
        public org.springframework.security.oauth2.core.OAuth2TokenValidatorResult validate(Jwt jwt) {
            List<String> tokenAudiences = jwt.getClaimAsStringList(JwtClaimNames.AUD);
            if (tokenAudiences != null) {
                for (String accepted : expectedAudiences.split(",")) {
                    if (tokenAudiences.contains(accepted.trim())) {
                        return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
                    }
                }
            }
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(ERROR);
        }
    }
}
