package com.example.serviceusers.users.api;

import java.util.List;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        Boolean emailVerified,
        List<String> requiredActions,
        String username,
        String password,
        Boolean isPasswordTemporary,
        String birthdate
        ) {
}
