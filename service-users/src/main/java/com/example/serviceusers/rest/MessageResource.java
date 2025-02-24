package com.example.serviceusers.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MessageResource {
    OK(HttpStatus.OK,"general.ok"),
    CREATED(HttpStatus.CREATED,"general.created"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"general.badRequest"),
    NOT_FOUND(HttpStatus.NOT_FOUND,"general.notFound"),
    FORBIDDEN(HttpStatus.FORBIDDEN,"general.forbidden"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"general.unauthorized"),
    CONFLICT(HttpStatus.CONFLICT,"general.conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"general.internalServerError");

    private final HttpStatus httpStatus;
    private final String message;

}
