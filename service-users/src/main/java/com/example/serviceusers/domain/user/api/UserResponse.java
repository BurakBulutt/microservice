package com.example.serviceusers.domain.user.api;

import com.example.serviceusers.domain.user.model.Role;
import com.example.serviceusers.domain.usergroup.dto.UserGroupDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private Boolean isEnabled;
    private Boolean isVerified;
    private UserGroupDto userGroupDto;
}
