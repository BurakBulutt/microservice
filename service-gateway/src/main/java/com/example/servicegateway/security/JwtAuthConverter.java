package com.example.servicegateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthConverter implements Converter<Jwt, Mono<JwtAuthenticationToken>> {

    @Override
    public Mono<JwtAuthenticationToken> convert(@NonNull Jwt jwt) {
        return Mono.just(new JwtAuthenticationToken(jwt, getAuthorities(jwt)));
    }

    public List<? extends GrantedAuthority> getAuthorities(Jwt jwt) {
        try {
            Map<String, Object> claims = jwt.getClaims();
            Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            List<String> roles = (List<String>) realmAccess.get("roles");
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> "ROLE_" + role)
                    .map(role -> (GrantedAuthority) () -> role)
                    .toList();
            return authorities;
        }catch (NullPointerException e) {
            return Collections.emptyList();
        }
    }
}
