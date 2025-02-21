package com.example.serviceusers.users.api;

import com.example.serviceusers.users.mapper.UserMapper;
import com.example.serviceusers.users.service.UserServiceImpl;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl service;

    @GetMapping
    public ResponseEntity<List<UserRepresentationResponse>> getAllUsers(@RequestParam(defaultValue = "0" ) int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(UserMapper.toUserRepresentationResponses(service.getAllUsers(page, size)));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserRepresentationResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserMapper.toUserRepresentationResponse(service.getUserByUsername(username)));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        service.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetUserPassword(@PathVariable String id) {
        service.resetUserPassword(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verify-email")
    public ResponseEntity<Void> sendVerifyEmail(@PathVariable String id) {
        service.sendVerifyEmail(id);
        return ResponseEntity.ok().build();
    }
}
