package com.example.serviceusers.domain.usergroup.dto;


import com.example.serviceusers.domain.user.dto.UserDto;
import com.example.serviceusers.domain.user.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupDto {
    private String id;
    private String name;
    private Role role;
    private List<UserDto> userList;
}
