package com.example.servicegateway.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

import static com.example.servicegateway.security.SecurityConstants.*;

@Configuration
@EnableWebFluxSecurity
@Profile("default")
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.authorizeExchange(authorizeExchangeSpec -> {
            authorizeExchangeSpec.pathMatchers("/actuator/**").permitAll();
            authorizeExchangeSpec.pathMatchers("/contents/**").hasRole(ROLE_ADMIN);
            authorizeExchangeSpec.pathMatchers("/medias/**").hasRole(ROLE_ADMIN);
            authorizeExchangeSpec.pathMatchers("/users/**").hasRole(ROLE_ADMIN);
            authorizeExchangeSpec.anyExchange().permitAll();
        });
        http.cors(corsSpec -> corsSpec.configurationSource(exchange -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
            corsConfiguration.setAllowCredentials(Boolean.TRUE);
            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
            return corsConfiguration;
        }));
        http.oauth2ResourceServer(oAuth2ResourceServerSpec ->
                oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(reactiveConverter())));
        return http.build();
    }

    private ReactiveJwtAuthenticationConverter reactiveConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

        ReactiveJwtGrantedAuthoritiesConverterAdapter reactiveJwtGrantedAuthoritiesConverterAdapter =
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(grantedAuthoritiesConverter);

        ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        reactiveJwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(reactiveJwtGrantedAuthoritiesConverterAdapter);
        return reactiveJwtAuthenticationConverter;
    }
}
