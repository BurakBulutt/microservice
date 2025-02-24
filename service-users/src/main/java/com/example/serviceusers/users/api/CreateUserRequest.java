package com.example.serviceusers.users.api;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        Boolean emailVerified,
        String username,
        String password,
        Boolean isPasswordTemporary,
        String birthdate
        ) {
}
