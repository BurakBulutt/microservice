package com.example.serviceusers.domain.user.service;

import com.example.serviceusers.domain.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Page<UserDto> getAll(Pageable pageable);
    Page<UserDto> filter(Pageable pageable,String username,Boolean isEnabled,Boolean isVerified);
    UserDto getById(String id);
    UserDto getByUsername(String username);
    UserDto save(UserDto userDto);
    UserDto update(String id,UserDto userDto);
    void delete (String id);
    Long count();
}
