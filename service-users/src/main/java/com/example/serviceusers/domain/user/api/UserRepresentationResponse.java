package com.example.serviceusers.domain.user.api;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class UserRepresentationResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private LocalDate birthdate;
    private Long createdTimestamp;
    private Boolean enabled;
}
