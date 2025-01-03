package com.example.serviceauth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthTestController {
    @GetMapping("test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test Api");
    }
}
