package com.example.servicegateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@Profile("prod")
public class SecurityProdConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                .anyExchange().permitAll()
        );
        // TODO CORS REQUIRED

        return http.build();
    }
}
