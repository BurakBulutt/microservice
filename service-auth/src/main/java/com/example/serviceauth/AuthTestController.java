package com.example.serviceauth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthTestController {

    @GetMapping("get-all")
    @PreAuthorize("hasAnyRole('user','admin')")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test Api");
    }

    @GetMapping("create")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<String> testCreate() {
        return ResponseEntity.ok("Test Create Api");
    }
}
