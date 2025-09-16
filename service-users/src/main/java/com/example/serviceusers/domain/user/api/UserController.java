package com.example.serviceusers.domain.user.api;

import com.example.serviceusers.domain.user.mapper.UserApiMapper;
import com.example.serviceusers.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<UserRepresentationResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(UserApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<UserRepresentationResponse>> filter(
            Pageable pageable,
            @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(UserApiMapper.toPageResponse(service.filter(pageable, username)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserRepresentationResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(UserApiMapper.toUserRepresentationResponse(service.getById(id)));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserRepresentationResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserApiMapper.toUserRepresentationResponse(service.getByUsername(username)));
    }

    @GetMapping("count")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Integer> count() {
        return ResponseEntity.ok(service.count());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> save(@Valid @RequestBody CreateUserRequest request) {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/update-profile")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> updateProfile(@PathVariable String id, @Valid @RequestBody UpdateProfileRequest request) {
        service.updateProfile(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> resetPassword(@PathVariable String id) {
        service.resetPassword(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@PathVariable String id, @Valid @RequestBody ChangePasswordRequest request) {
        service.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-email")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> sendVerifyEmail(@PathVariable String id) {
        service.verifyEmail(id);
        return ResponseEntity.noContent().build();
    }
}
