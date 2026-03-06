package com.example.serviceusers.domain.usergroup.service;


import com.example.serviceusers.domain.user.mapper.UserServiceMapper;
import com.example.serviceusers.domain.user.model.Role;
import com.example.serviceusers.domain.usergroup.dto.UserGroupDto;
import com.example.serviceusers.domain.usergroup.mapper.UserGroupServiceMapper;
import com.example.serviceusers.domain.usergroup.model.UserGroup;
import com.example.serviceusers.domain.usergroup.repo.UserGroupRepository;
import com.example.serviceusers.utilities.exception.BaseException;
import com.example.serviceusers.utilities.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS)
public class UserGroupServiceImpl implements UserGroupService{
    private final UserGroupRepository repository;

    @Override
    public Page<UserGroupDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(UserGroupServiceMapper::toDto);
    }

    @Override
    public Page<UserGroupDto> filter(Pageable pageable, String name, Role role) {
        return Page.empty();
    }

    @Override
    public UserGroupDto getById(String id) {
        UserGroup userGroup = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserGroup.class.getSimpleName(),id));
        UserGroupDto userGroupDto = UserGroupServiceMapper.toDto(userGroup);
        userGroupDto.setUserList(userGroup.getUserList().stream().map(UserServiceMapper::toUserDto).toList());
        return userGroupDto;
    }

    @Override
    public UserGroup findById(String id) {
        return repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserGroup.class.getSimpleName(),id));
    }

    @Override
    @Transactional
    public UserGroupDto save(UserGroupDto userGroupDto) {
        return UserGroupServiceMapper.toDto(repository.save(UserGroupServiceMapper.toEntity(new UserGroup(),userGroupDto)));
    }

    @Override
    @Transactional
    public UserGroupDto update(String id, UserGroupDto userGroupDto) {
        UserGroup userGroup = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserGroup.class.getSimpleName(),id));
        return UserGroupServiceMapper.toDto(repository.save(UserGroupServiceMapper.toEntity(userGroup,userGroupDto)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        UserGroup userGroup = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserGroup.class.getSimpleName(),id));
        repository.delete(userGroup);
    }
}
