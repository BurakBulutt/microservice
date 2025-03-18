package com.example.servicemedia.util.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageResource {
    OK(200,"general.ok"),
    CREATED(201,"general.created"),
    BAD_REQUEST(400,"general.badRequest"),
    NOT_FOUND(404,"general.notFound"),
    FORBIDDEN(403,"general.forbidden"),
    UNAUTHORIZED(401,"general.unauthorized"),
    CONFLICT(409,"general.conflict"),
    INTERNAL_SERVER_ERROR(500,"general.internalServerError"),
    SERVICE_UNAVAILABLE(503,"general.serviceUnavailable");

    private final int status;
    private final String message;

}
