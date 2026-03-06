package com.example.serviceusers.domain.usergroup.mapper;

import com.example.serviceusers.domain.usergroup.api.UserGroupRequest;
import com.example.serviceusers.domain.usergroup.api.UserGroupResponse;
import com.example.serviceusers.domain.usergroup.dto.UserGroupDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGroupApiMapper {

    public static UserGroupResponse toResponse(UserGroupDto userGroupDto) {
        return UserGroupResponse.builder()
                .id(userGroupDto.getId())
                .name(userGroupDto.getName())
                .role(userGroupDto.getRole())
                .userList(userGroupDto.getUserList())
                .build();
    }

    public static UserGroupDto toDto(UserGroupRequest request) {
        return UserGroupDto.builder()
                .name(request.name())
                .role(request.role())
                .build();
    }

    public static Page<UserGroupResponse> toPageResponse(Page<UserGroupDto> userGroupDtoPage) {
        return userGroupDtoPage.map(UserGroupApiMapper::toResponse);
    }
}
