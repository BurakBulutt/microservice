package com.example.serviceauth.user.api;

import java.util.List;
import java.util.Map;

public record UpdateRequest(
        String firstName,
        String lastName,
        Map<String, List<String>> attributes
) {
}
