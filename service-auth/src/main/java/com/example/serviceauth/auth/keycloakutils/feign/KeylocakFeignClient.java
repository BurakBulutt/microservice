package com.example.serviceauth.auth.keycloakutils.feign;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "keycloakFeign",url = "http://localhost:8080",configuration = KeycloakFeignConfiguration.class)
public interface KeylocakFeignClient {

    @PostMapping(value = "/realms/{realm}/protocol/openid-connect/token",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenResponse getAccessToken(@PathVariable String realm, @RequestBody Map<String, String> form);
}
