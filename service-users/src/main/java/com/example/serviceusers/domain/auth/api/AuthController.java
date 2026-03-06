package com.example.serviceusers.domain.auth.api;

import com.example.serviceusers.domain.auth.mapper.AuthApiMapper;
import com.example.serviceusers.domain.auth.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(AuthApiMapper.toResponse(authService.login(AuthApiMapper.toDto(request))));
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthApiMapper.toResponse(authService.register(AuthApiMapper.toDto(request))));
    }
}
