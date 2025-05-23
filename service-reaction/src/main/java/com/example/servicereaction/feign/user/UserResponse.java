package com.example.servicereaction.feign.user;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
}
