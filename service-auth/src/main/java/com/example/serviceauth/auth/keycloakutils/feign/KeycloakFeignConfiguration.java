package com.example.serviceauth.auth.keycloakutils.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakFeignConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new KeycloakErrorDecoder();
    }
}
