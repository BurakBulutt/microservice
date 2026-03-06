package com.example.serviceusers.domain.user.api;

import com.example.serviceusers.domain.user.model.Role;
import jakarta.validation.constraints.*;

public record SaveUserRequest(
        @NotBlank(message = "user.firstName.notBlank")
        String firstName,

        @NotBlank(message = "user.lastName.notBlank")
        String lastName,

        @NotBlank(message = "user.username.notBlank")
        String username,

        Role role,

        @NotNull(message = "user.password.notNull")
        @Size(min = 8, message = "user.password.size")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
                message = "user.password.pattern"
        )
        String password,

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
