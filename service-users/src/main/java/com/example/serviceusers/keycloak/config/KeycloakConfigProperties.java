package com.example.serviceusers.keycloak.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfigProperties {
    private String authServerUrl;
    private String realm;
    private String resource;
    private Credentials credentials;

    @Getter
    @Setter
    public static class Credentials {
        private String secret;
    }
}
