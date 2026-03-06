package com.example.serviceusers.domain.usergroup.repo;

import com.example.serviceusers.domain.usergroup.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup,String> {
    Optional<UserGroup> findUserGroupByName(String name);
}
