package com.example.servicegateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.authorizeExchange(authorizeExchangeSpec -> {
           authorizeExchangeSpec.pathMatchers("api/v1/auth/**").hasAnyRole("USER", "ADMIN");
           authorizeExchangeSpec.anyExchange().authenticated();
        });
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(corsSpec -> corsSpec.configurationSource(exchange -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
            corsConfiguration.setAllowCredentials(Boolean.TRUE);
            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        //    corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
            return corsConfiguration;
        }));
        http.oauth2ResourceServer(oAuth2ResourceServerSpec ->
                oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(new JwtAuthConverter())));
        return http.build();
    }
}
