package com.example.serviceusers.domain.usergroup.service;


import com.example.serviceusers.domain.user.model.Role;
import com.example.serviceusers.domain.usergroup.dto.UserGroupDto;
import com.example.serviceusers.domain.usergroup.model.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserGroupService {
    Page<UserGroupDto> getAll(Pageable pageable);
    Page<UserGroupDto> filter(Pageable pageable, String name, Role role);
    UserGroupDto getById(String id);
    UserGroupDto save(UserGroupDto userGroupDto);
    UserGroupDto update(String id, UserGroupDto userGroupDto);
    void delete(String id);

    UserGroup findById(String id);
}
