package com.example.servicegateway.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@Profile("default")
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                .anyExchange().permitAll()
        );
        http.cors(corsSpec -> corsSpec.configurationSource(exchange -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173","http://localhost"));
            corsConfiguration.setAllowCredentials(Boolean.TRUE);
            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
            return corsConfiguration;
        }));

        return http.build();
    }
}
