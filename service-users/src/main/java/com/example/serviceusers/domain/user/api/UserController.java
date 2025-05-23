package com.example.serviceusers.domain.user.api;

import com.example.serviceusers.domain.user.mapper.UserApiMapper;
import com.example.serviceusers.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<Page<UserRepresentationResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(UserApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    public ResponseEntity<Page<UserRepresentationResponse>> filter(Pageable pageable,
                                                                   @RequestParam(required = false) String username) {
        return ResponseEntity.ok(UserApiMapper.toPageResponse(service.filter(pageable, username)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRepresentationResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(UserApiMapper.toUserRepresentationResponse(service.getById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserRepresentationResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserApiMapper.toUserRepresentationResponse(service.getByUsername(username)));
    }

    @GetMapping("count")
    public ResponseEntity<Integer> count() {
        return ResponseEntity.ok(service.count());
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserRequest request) {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/update-profile")
    public ResponseEntity<Void> updateProfile(@PathVariable String id, @RequestBody @Valid UpdateProfileRequest request) {
        service.updateProfile(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetUserPassword(@PathVariable String id) {
        service.resetPassword(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable String id,@RequestBody @Valid ChangePasswordRequest request) {
        service.changePassword(id,request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-email")
    public ResponseEntity<Void> sendVerifyEmail(@PathVariable String id) {
        service.verifyEmail(id);
        return ResponseEntity.noContent().build();
    }
}
