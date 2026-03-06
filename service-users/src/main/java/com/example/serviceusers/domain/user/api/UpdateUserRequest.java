package com.example.serviceusers.domain.user.api;

import com.example.serviceusers.domain.user.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "user.firstName.notBlank")
        String firstName,

        @NotBlank(message = "user.lastName.notBlank")
        String lastName,

        @NotBlank(message = "user.username.notBlank")
        String username,

        Role role,

        @NotBlank(message = "user.email.notBlank")
        @Email(message = "user.email.emailFormat")
        String email,

        @NotNull(message = "user.isEnabled.notNull")
        Boolean isEnabled,

        @NotNull(message = "user.isVerified.notNull")
        Boolean isVerified,

        String userGroupId
) {
}
