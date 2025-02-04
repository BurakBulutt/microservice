package com.example.serviceauth.auth.keycloakutils.feign;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomKeycloakException extends RuntimeException {
    private final String error;
    private final String errorDescription;
    private final HttpStatus httpStatus;

    public CustomKeycloakException(String error, String errorDescription, HttpStatus httpStatus) {
        super("error: " + error + ", errorDescription: " + errorDescription);
        this.error = error;
        this.errorDescription = errorDescription;
        this.httpStatus = httpStatus;
    }
}
