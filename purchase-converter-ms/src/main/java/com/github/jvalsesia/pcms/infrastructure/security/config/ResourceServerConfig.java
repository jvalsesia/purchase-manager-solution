package com.github.jvalsesia.pcms.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerConfig {
    private final IssuerUriConfig issuerUriConfig;

    public ResourceServerConfig(IssuerUriConfig issuerUriConfig) {
        this.issuerUriConfig = issuerUriConfig;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/purchases/{id}")
                        .hasAnyAuthority("SCOPE_purchase")
                        .requestMatchers(HttpMethod.POST, "/api/purchases")
                        .hasAnyAuthority("SCOPE_purchase")
                        .anyRequest().authenticated());

        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String issuerUri = issuerUriConfig.getProtocol() + "://" + issuerUriConfig.getHost() + ":"
                + issuerUriConfig.getPort() + "/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(issuerUri).build();
    }

}
