package com.example.serviceusers.domain.usergroup.mapper;

import com.example.serviceusers.domain.usergroup.dto.UserGroupDto;
import com.example.serviceusers.domain.usergroup.model.UserGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGroupServiceMapper {

    public static UserGroupDto toDto(UserGroup userGroup) {
        return UserGroupDto.builder()
                .id(userGroup.getId())
                .name(userGroup.getName())
                .role(userGroup.getRole())
                .build();
    }

    public static UserGroup toEntity(UserGroup userGroup, UserGroupDto userGroupDto) {
        userGroup.setName(userGroupDto.getName());
        userGroup.setRole(userGroupDto.getRole());

        return userGroup;
    }
}
