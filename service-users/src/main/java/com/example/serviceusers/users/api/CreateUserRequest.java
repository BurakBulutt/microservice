package com.example.serviceusers.users.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        String firstName,
        String lastName,
        @NotNull(message = "validation.user.email.null")
        @NotEmpty(message = "validation.user.email.empty")
        @Email(message = "validation.user.email.invalid")
        String email,
        Boolean enabled,
        Boolean emailVerified,
        @NotNull(message = "validation.user.username.null")
        @NotEmpty(message = "validation.user.username.empty")
        String username,
        String password,
        Boolean isPasswordTemporary,
        String birthdate
        ) {
}
