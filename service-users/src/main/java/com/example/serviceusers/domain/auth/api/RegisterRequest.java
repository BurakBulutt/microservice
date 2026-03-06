package com.example.serviceusers.domain.auth.api;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "user.firstName.notBlank")
        String firstName,

        @NotBlank(message = "user.lastName.notBlank")
        String lastName,

        @NotBlank(message = "auth.username.notBlank")
        String username,

        @NotNull(message = "user.password.notNull")
        @Size(min = 8, message = "user.password.size")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
                message = "user.password.pattern"
        )
        String password,

        @NotBlank(message = "user.email.notBlank")
        @Email(message = "user.email.emailFormat")
        String email
) {
}
