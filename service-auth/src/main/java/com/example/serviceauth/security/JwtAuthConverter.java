package com.example.serviceauth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String CLIENT = "microservice-auth";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        return new JwtAuthenticationToken(source,getAuthorities(source));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Jwt jwt) {
        if (jwt.getClaims().get(RESOURCE_ACCESS) != null) {
            Map<String, Object> resourceMap = jwt.getClaim(RESOURCE_ACCESS);

            if(resourceMap.get(CLIENT) != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> clientMap = mapper.convertValue(resourceMap.get(CLIENT), Map.class);
                List<String> roles = mapper.convertValue(clientMap.get(ROLES), ArrayList.class);
                List<GrantedAuthority> authorities = new ArrayList<>();
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));

                return authorities;
            }
        }
        return Collections.emptyList();
    }
}
