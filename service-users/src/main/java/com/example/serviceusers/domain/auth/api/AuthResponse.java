package com.example.serviceusers.domain.auth.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
}
