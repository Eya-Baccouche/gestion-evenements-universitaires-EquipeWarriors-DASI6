package com.warriors.fidelite.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Profil "dev" : AUCUN Keycloak. Chaque requete est executee comme l'utilisateur fictif
 * "user-123" avec les roles ADMIN, ORGANISATEUR et PARTICIPANT.
 * Pour tester un autre utilisateur (Postman) : en-tetes X-User-Id et X-Roles (ex: "PARTICIPANT").
 * NE JAMAIS utiliser en production.
 */
@Configuration
@Profile("dev")
@EnableMethodSecurity
public class DevSecurityConfig {

    @Bean
    SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(new FakeUserFilter(), AnonymousAuthenticationFilter.class);
        return http.build();
    }

    static class FakeUserFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            String userId = valeurOuDefaut(request.getHeader("X-User-Id"), "user-123");
            String roles = valeurOuDefaut(request.getHeader("X-Roles"), "ADMIN,ORGANISATEUR,PARTICIPANT");

            List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                    .toList();

            Jwt jwt = Jwt.withTokenValue("dev-token")
                    .header("alg", "none")
                    .subject(userId)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new JwtAuthenticationToken(jwt, authorities));
            SecurityContextHolder.setContext(context);
            chain.doFilter(request, response);
        }

        private String valeurOuDefaut(String valeur, String defaut) {
            return (valeur == null || valeur.isBlank()) ? defaut : valeur;
        }
    }
}
