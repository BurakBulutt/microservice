package com.example.serviceusers.domain.auth.api;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email
) {
}
