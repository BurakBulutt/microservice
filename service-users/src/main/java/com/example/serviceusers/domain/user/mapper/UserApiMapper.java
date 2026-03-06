package com.example.serviceusers.domain.user.mapper;

import com.example.serviceusers.domain.user.api.SaveUserRequest;
import com.example.serviceusers.domain.user.api.UpdateUserRequest;
import com.example.serviceusers.domain.user.api.UserResponse;
import com.example.serviceusers.domain.user.dto.UserDto;
import com.example.serviceusers.domain.usergroup.dto.UserGroupDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserApiMapper {

    public static UserResponse toResponse(UserDto userDto) {
        return UserResponse.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .isEnabled(userDto.getIsEnabled())
                .isVerified(userDto.getIsVerified())
                .userGroupDto(userDto.getUserGroupDto())
                .build();
    }

    public static UserDto toDto(SaveUserRequest request) {
        return UserDto.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .password(request.password())
                .email(request.email())
                .isVerified(request.isVerified())
                .isEnabled(request.isEnabled())
                .role(request.role())
                .userGroupDto(request.userGroupId() != null && !request.userGroupId().isBlank() ? UserGroupDto.builder().id(request.userGroupId()).build() : null)
                .build();
    }

    public static UserDto toDto(UpdateUserRequest request) {
        return UserDto.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .isVerified(request.isVerified())
                .isEnabled(request.isEnabled())
                .role(request.role())
                .userGroupDto(request.userGroupId() != null && !request.userGroupId().isBlank() ? UserGroupDto.builder().id(request.userGroupId()).build() : null)
                .build();
    }

    public static Page<UserResponse> toPageResponse(Page<UserDto> userDtoPage) {
        return userDtoPage.map(UserApiMapper::toResponse);
    }
}
