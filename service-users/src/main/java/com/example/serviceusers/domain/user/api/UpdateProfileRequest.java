package com.example.serviceusers.domain.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateProfileRequest(
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
        @NotNull(message = "validation.birthdate.null")
        LocalDate birthdate
        ) {
}
