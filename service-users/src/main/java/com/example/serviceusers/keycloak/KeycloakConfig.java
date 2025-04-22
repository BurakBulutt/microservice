package com.example.serviceusers.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private final KeycloakConfigProperties configProperties;

    @Bean
    public Keycloak initKeycloak() {
        return KeycloakBuilder.builder()
                .realm(configProperties.getRealm())
                .serverUrl(configProperties.getAuthServerUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(configProperties.getClientId())
                .clientSecret(configProperties.getClientSecret())
                .build();
    }

    @Bean
    public UsersResource usersResource(Keycloak keycloak) {
        return keycloak.realm(configProperties.getRealm()).users();
    }
}
