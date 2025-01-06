package com.example.servicegateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers("/auth/**").permitAll();
                    authorizeExchangeSpec.anyExchange().authenticated();
                })
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwtDecoder(reactiveJwtDecoder())))
                .addFilterAfter(policyWebFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return new NimbusReactiveJwtDecoder(jwkSetUri);
    }

    @Bean
    public WebFilter policyWebFilter() {
        return new ReactivePolicyEnforcerFilter();
    }
}
