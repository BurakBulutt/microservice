package com.example.servicemedia.util.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class RestExceptionHandler {
    private final MessageSource messageSource;


    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException e, Locale locale) {
        String message = StringUtils.hasLength(e.getMessage()) ? e.getMessage()
                : messageSource.getMessage(e.getMessageResource().getMessage(), e.getArgs(), locale);
        log.error(message);
        return ResponseEntity.status(e.getMessageResource().getHttpStatus()).body(message);
    }
}
