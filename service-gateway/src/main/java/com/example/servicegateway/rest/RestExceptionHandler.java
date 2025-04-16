package com.example.servicegateway.rest;

import io.netty.channel.ConnectTimeoutException;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGatewayNotFound(NotFoundException e,ServerWebExchange exchange) {
        HttpStatus status = e.getStatusCode().value() == 404 ? HttpStatus.NOT_FOUND : HttpStatus.SERVICE_UNAVAILABLE;
        return Mono.just(ResponseEntity.status(status)
                .body(new ErrorResponse(
                        exchange.getRequest().getPath().value(),
                        status.getReasonPhrase(),
                        e.getLocalizedMessage(),
                        LocalDateTime.now()
                )));
    }

    @ExceptionHandler(TimeoutException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTimeoutException(TimeoutException e,ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new ErrorResponse(
                        exchange.getRequest().getPath().value(),
                        HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                        e.getLocalizedMessage(),
                        LocalDateTime.now()
                )));
    }

    @ExceptionHandler(ConnectTimeoutException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTimeoutException(ConnectTimeoutException e,ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new ErrorResponse(
                        exchange.getRequest().getPath().value(),
                        HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                        e.getLocalizedMessage(),
                        LocalDateTime.now()
                )));
    }

}
