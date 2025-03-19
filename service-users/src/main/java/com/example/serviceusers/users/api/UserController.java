package com.example.serviceusers.users.api;

import com.example.serviceusers.users.mapper.UserMapper;
import com.example.serviceusers.users.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserServiceImpl service;

    @GetMapping
    public ResponseEntity<Page<UserRepresentationResponse>> getAllUsers(@RequestParam(defaultValue = "0" ) int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(UserMapper.toPageResponse(service.getAllUsers(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRepresentationResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(UserMapper.toUserRepresentationResponse(service.getUserById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserRepresentationResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserMapper.toUserRepresentationResponse(service.getUserByUsername(username)));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetUserPassword(@PathVariable String id) {
        service.resetUserPassword(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-email")
    public ResponseEntity<Void> sendVerifyEmail(@PathVariable String id) {
        service.sendVerifyEmail(id);
        return ResponseEntity.noContent().build();
    }
}
