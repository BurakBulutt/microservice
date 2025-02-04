package com.example.serviceauth.rest;

import com.example.serviceauth.auth.keycloakutils.feign.CustomKeycloakException;
import com.example.serviceauth.auth.keycloakutils.feign.KeycloakErrorResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getResponse().getStatusInfo().getReasonPhrase(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException ex) {
        return new ResponseEntity<>(ex.getResponse().getStatusInfo().getReasonPhrase(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getResponse().getStatusInfo().getReasonPhrase(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomKeycloakException.class)
    public ResponseEntity<KeycloakErrorResponse> handleBadRequestException(CustomKeycloakException ex) {
        KeycloakErrorResponse keycloakErrorResponse = new KeycloakErrorResponse();
        keycloakErrorResponse.setError(ex.getError());
        keycloakErrorResponse.setErrorDescription(ex.getErrorDescription());
        return new ResponseEntity<>(keycloakErrorResponse,ex.getHttpStatus());
    }

}
