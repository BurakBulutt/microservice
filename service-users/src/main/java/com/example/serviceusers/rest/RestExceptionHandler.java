package com.example.serviceusers.rest;

import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        Map<String,String> validationErrors = new HashMap<>();
        List<ObjectError> errorList = ex.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            String field = ((FieldError) error).getField();
            String validationMessage= error.getDefaultMessage();
            validationErrors.put(field, validationMessage);
        });

        return ResponseEntity.status(status).body(validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e,WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getLocalizedMessage(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(WebApplicationException.class)
    public ResponseEntity<ErrorResponse> handleWebApplication(WebApplicationException e,WebRequest request) {
        return ResponseEntity.status(e.getResponse().getStatus()).body(new ErrorResponse(
                request.getDescription(false),
                e.getResponse().getStatusInfo().getReasonPhrase(),
                e.getLocalizedMessage(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e,WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                request.getDescription(false),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getLocalizedMessage(),
                LocalDateTime.now()
        ));
    }
}
