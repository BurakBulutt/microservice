package com.example.servicereaction.feign;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
}
