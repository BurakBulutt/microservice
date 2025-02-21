package com.example.servicegateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

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
        http.oauth2ResourceServer(oAuth2ResourceServerSpec -> {
            oAuth2ResourceServerSpec.jwt(jwtSpec -> {
                jwtSpec.jwtAuthenticationConverter(new JwtAuthConverter());
            });
        });
        return http.build();
    }
}
