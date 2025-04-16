package com.example.serviceusers.users.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        String firstName,
        String lastName,
        @NotNull(message = "validation.email.null")
        @NotEmpty(message = "validation.email.empty")
        @Email(message = "validation.email.invalid")
        String email,
        Boolean enabled,
        Boolean emailVerified,
        @NotNull(message = "validation.username.null")
        @NotEmpty(message = "validation.username.empty")
        String username,
        String password,
        Boolean isPasswordTemporary,
        String birthdate
        ) {
}
