package com.example.servicemedia.config.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String AUTHORITIES_CLAIM_NAME = "resource_access";
    private static final Collection<String> WELL_KNOWN_CLIENT_NAMES = Arrays.asList("app-admin-client","app-fe-client");


    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        if (getAuthorities(jwt).isEmpty()) {
            return Collections.emptyList();
        }

        return getAuthorities(jwt).stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority))
                .collect(Collectors.toList());
    }

    public Collection<String> getAuthorities(Jwt jwt) {
        if (!jwt.hasClaim(AUTHORITIES_CLAIM_NAME)) {
            return Collections.emptyList();
        }

        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get(AUTHORITIES_CLAIM_NAME);
        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Collections.emptyList();
        }

        return getClientRole(resourceAccess) != null  ? (Collection<String>) getClientRole(resourceAccess).get("roles") : Collections.emptyList();
    }

    public Map<String,Object> getClientRole(Map<String,Object> resourceAccess) {
        for (String name : WELL_KNOWN_CLIENT_NAMES) {
            if (resourceAccess.containsKey(name)) {
                return (Map<String, Object>) resourceAccess.get(name);
            }
        }
        return null;
    }
}
