package com.example.serviceusers.domain.user.api;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotNull(message = "validation.firstName.null")
        @NotBlank(message = "validation.firstName.blank")
        String firstName,
        @NotNull(message = "validation.lastName.null")
        @NotBlank(message = "validation.lastName.blank")
        String lastName,
        @NotNull(message = "validation.email.null")
        @NotBlank(message = "validation.email.blank")
        @Email(message = "validation.email.invalidEmail")
        String email,
        @NotNull(message = "validation.enabled.null")
        Boolean enabled,
        @NotNull(message = "validation.emailVerified.null")
        Boolean emailVerified,
        @NotNull(message = "validation.username.null")
        @NotBlank(message = "validation.username.blank")
        String username,
        @NotNull(message = "validation.password.null")
        @NotBlank(message = "validation.password.blank")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])[A-Za-z0-9\\s~!@#$%^&*()_+\\-={}:;\"',<.>/?]{8,}$", message = "validation.password.invalidPattern")
        String password,
        @NotNull(message = "validation.isPasswordTemporary.null")
        Boolean isPasswordTemporary,
        @NotNull(message = "validation.birthdate.null")
        LocalDate birthdate
        ) {
}
