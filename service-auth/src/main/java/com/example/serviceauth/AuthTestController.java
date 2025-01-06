package com.example.serviceauth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class AuthTestController {
    @GetMapping("get-all")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test Api");
    }

    @GetMapping("create")
    public ResponseEntity<String> testCreate() {
        return ResponseEntity.ok("Test Create Api");
    }
}
