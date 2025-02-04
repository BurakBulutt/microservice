package com.example.serviceauth.auth.keycloakutils.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KeycloakErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (response.body() != null && response.status() == 400) {
            try {
                KeycloakErrorResponse errorResponse = objectMapper.readValue(
                        response.body().asInputStream(), KeycloakErrorResponse.class
                );
                return new CustomKeycloakException(errorResponse.getError(), errorResponse.getErrorDescription(), HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                return defaultErrorDecoder.decode(methodKey, response);
            }
        } else {
            if (response.status() == 401) {
                return new CustomKeycloakException("invalid_grant", "Invalid user credentials", HttpStatus.UNAUTHORIZED);
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
