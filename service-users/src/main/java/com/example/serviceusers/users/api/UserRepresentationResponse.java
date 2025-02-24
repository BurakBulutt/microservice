package com.example.serviceusers.users.api;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserRepresentationResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private String birthdate;
    private Long createdTimestamp;
    private Boolean enabled;
}
