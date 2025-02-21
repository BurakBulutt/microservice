package com.example.serviceusers.users.api;

import java.util.List;
import java.util.Map;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        Boolean emailVerified,
        List<String> requiredActions,
        String birthdate
        ) {
}
