package com.example.serviceusers.domain.user.mapper;

import com.example.serviceusers.domain.user.dto.UserDto;
import com.example.serviceusers.domain.user.model.User;
import com.example.serviceusers.domain.usergroup.mapper.UserGroupServiceMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .isVerified(user.getIsVerified())
                .userGroupDto(user.getUserGroup() != null ? UserGroupServiceMapper.toDto(user.getUserGroup()) : null)
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .isVerified(user.getIsVerified())
                .build();
    }

    public static User toEntity(User user, UserDto userDto) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setIsEnabled(userDto.getIsEnabled());
        user.setIsVerified(userDto.getIsVerified());

        return user;
    }
}
