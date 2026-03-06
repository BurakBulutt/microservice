package com.example.serviceusers.domain.usergroup.api;


import com.example.serviceusers.domain.user.model.Role;

public record UserGroupRequest(
        String name,
        Role role
) {
}
