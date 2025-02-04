package com.example.serviceauth.user.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserRepresentationResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes;
}
