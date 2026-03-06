package com.example.serviceusers.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
}
