package com.example.serviceusers.domain.auth.api;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "auth.username.notBlank")
        String username,

        @NotBlank(message = "auth.password.notBlank")
        String password
) {
}
