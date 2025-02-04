package com.example.serviceauth.user.api;

public record RegisterRequest(
        String username,
        String name,
        String surname,
        String password,
        String email,
        String birthdate
) {
}
