package com.example.servicegateway.rest;

import java.time.LocalDateTime;

public record ErrorResponse(
        String path,
        String error,
        String message,
        LocalDateTime timestamp
) {
}
