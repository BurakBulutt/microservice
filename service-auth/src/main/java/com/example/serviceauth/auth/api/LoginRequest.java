package com.example.serviceauth.auth.api;

public record LoginRequest(
        String username,
        String password
) {
}
