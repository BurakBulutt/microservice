package com.example.servicereaction.util.rest;


import com.example.servicereaction.util.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class RestExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,WebRequest request) {
        Map<String,Object> validationError = new HashMap<>();
        Set<Map<String,String>> errors = new HashSet<>();
        validationError.put("path", request.getDescription(false));
        validationError.put("errors",errors);

        List<ObjectError> errorList = e.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), error.getArguments(), Locale.getDefault());
            errors.add(Map.of(field, message));
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e,WebRequest request) {
        String message = messageSource.getMessage(e.getMessageResource().getMessage(), e.getArgs(), Locale.getDefault());
        return ResponseEntity.status(e.getMessageResource().getStatus()).body(new ErrorResponse(
                request.getDescription(false),
                e.getMessageResource().name(),
                message,
                LocalDateTime.now()
        ));
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
}
