package com.example.serviceusers.domain.auth.mapper;

import com.example.serviceusers.domain.auth.api.AuthResponse;
import com.example.serviceusers.domain.auth.api.LoginRequest;
import com.example.serviceusers.domain.auth.api.RegisterRequest;
import com.example.serviceusers.domain.auth.dto.AuthDto;
import com.example.serviceusers.domain.auth.dto.LoginDto;
import com.example.serviceusers.domain.auth.dto.RegisterDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthApiMapper {

    public static LoginDto toDto(LoginRequest request) {
        return LoginDto.builder()
                .username(request.username())
                .password(request.password())
                .build();
    }

    public static RegisterDto toDto(RegisterRequest request) {
        return RegisterDto.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .password(request.password())
                .email(request.email())
                .build();
    }

    public static AuthResponse toResponse(AuthDto authDto) {
        return AuthResponse.builder()
                .accessToken(authDto.getToken())
                .build();
    }
}
