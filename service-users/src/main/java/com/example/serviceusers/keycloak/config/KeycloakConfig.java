package com.example.serviceusers.keycloak.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private final KeycloakConfigProperties configProperties;

    @Bean
    @Scope("prototype")
    public Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .realm(configProperties.getRealm())
                .serverUrl(configProperties.getAuthServerUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(configProperties.getClientId())
                .clientSecret(configProperties.getClientSecret())
                .build();
    }
}
