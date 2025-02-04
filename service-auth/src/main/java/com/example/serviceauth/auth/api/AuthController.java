package com.example.serviceauth.auth.api;

import com.example.serviceauth.user.dto.UserRepresentationMapper;
import com.example.serviceauth.auth.service.AuthService;
import com.example.serviceauth.user.api.RegisterRequest;
import com.example.serviceauth.user.api.UserRepresentationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ofNullable(UserRepresentationMapper.toTokenResponse(service.login(request)));
    }

    @PostMapping("register")
    public ResponseEntity<UserRepresentationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ofNullable(UserRepresentationMapper.toResponse(service.register(UserRepresentationMapper.toDto(request))));
    }

    @PostMapping("refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ofNullable(UserRepresentationMapper.toTokenResponse(service.refreshToken(request.token())));
    }

    @GetMapping("logout")
    public ResponseEntity<Void> refresh() {
        service.logout();
        return ResponseEntity.noContent().build();
    }
}
