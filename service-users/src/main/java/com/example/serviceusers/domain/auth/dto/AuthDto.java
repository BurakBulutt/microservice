package com.example.serviceusers.domain.auth.dto;

import com.example.serviceusers.domain.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDto {
    private String token;
    private UserDto userDto;
}
