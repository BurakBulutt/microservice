package com.example.serviceusers.users.api;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        Boolean emailVerified,
        String birthdate
        ) {
}
