package com.example.serviceauth.user.api;

import com.example.serviceauth.user.dto.UserRepresentationMapper;
import com.example.serviceauth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserRepresentationResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ofNullable(UserRepresentationMapper.toResponseList(service.getAllUsers(page, size)));
    }

    @GetMapping("get-by-username")
    public ResponseEntity<UserRepresentationResponse> getAllUsers(@RequestParam String username) {
        return ResponseEntity.ofNullable(UserRepresentationMapper.toResponse(service.getUserByUsername(username)));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<UserRepresentationResponse> updateUser(@PathVariable String id, @RequestBody UpdateRequest request) {
        return ResponseEntity.ofNullable(UserRepresentationMapper.toResponse(service.updateUser(id, request)));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
