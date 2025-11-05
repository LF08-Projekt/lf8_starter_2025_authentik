package de.szut.lf8_projekt.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Sicherheitskonfiguration für die Authentik-basierte JWT-Validierung.
 * Aktiviert Methodensicherheit, konfiguriert CORS/CSRF und schützt die Projektendpunkte.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@ConditionalOnProperty(value = "authentik.enabled", matchIfMissing = true)
public class AuthentikSecurityConfig {

    @Value("${authentik.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * Erstellt den {@link JwtDecoder} für die Validierung von JWTs anhand des JWK-Set-URIs.
     *
     * @return konfigurierter JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * Konfiguriert die HTTP-Sicherheitskette: CORS, CSRF und OAuth2 Resource Server (JWT).
     * Schützt die Endpunkte unter {@code /LF08Projekt/**}.
     *
     * @param http HttpSecurity-Builder
     * @return erstellte SecurityFilterChain
     * @throws Exception bei Konfigurationsfehlern
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> { })
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/LF08Projekt").authenticated()
                        .requestMatchers("/LF08Projekt/**").authenticated()
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * Basiskonfiguration für CORS, erlaubt Standard-Methoden und gängige Header.
     *
     * @return CORS-Configuration-Source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
