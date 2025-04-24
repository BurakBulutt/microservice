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

import static com.example.servicegateway.security.SecurityConstants.ROLE_ADMIN;
import static com.example.servicegateway.security.SecurityConstants.ROLE_USER;

@Configuration
@EnableWebFluxSecurity
@Profile("prod")
public class SecurityProdConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.authorizeExchange(authorizeExchangeSpec -> {
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET,"/api/users/**").hasRole(ROLE_USER);
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET,"/api/medias/**").hasRole(ROLE_USER);
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET,"/api/contents/**").hasRole(ROLE_USER);
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET,"/api/categories/**").hasRole(ROLE_USER);
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET,"/api/comments/**").hasRole(ROLE_USER);
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET,"/api/likes/**").hasRole(ROLE_USER);
            authorizeExchangeSpec.pathMatchers("/actuator/**").permitAll();
            authorizeExchangeSpec.pathMatchers("/api/**").hasRole(ROLE_ADMIN);
            authorizeExchangeSpec.anyExchange().denyAll();
        });
        //TODO CORS REQUIRED
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
