package com.example.serviceusers.domain.user.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotNull(message = "validation.password.null")
        @NotEmpty(message = "validation.password.empty")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])[A-Za-z0-9\\s~!@#$%^&*()_+\\-={}:;\"',<.>/?]{8,}$", message = "validation.password.invalidPattern")
        String newPassword
) {
}
