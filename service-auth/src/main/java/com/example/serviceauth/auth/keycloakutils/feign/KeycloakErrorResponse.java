package com.example.serviceauth.auth.keycloakutils.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KeycloakErrorResponse {
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
