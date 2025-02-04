package com.example.serviceauth.auth.keycloakutils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakProperties {
    private String realm;
    private String authServerUrl;
    private String resource;
    private Credentials credentials;

    @Data
    public static class Credentials {
        private String secret;
    }
}
