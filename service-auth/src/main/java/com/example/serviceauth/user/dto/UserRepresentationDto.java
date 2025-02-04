package com.example.serviceauth.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserRepresentationDto {
    private String id;
    private String username;
    private String email;
    private Boolean enabled;
    private Boolean emailVerified;
    private String firstName;
    private String lastName;
    private String password;
    private Map<String, List<String>> attributes;
}
