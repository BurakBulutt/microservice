package com.example.serviceusers.users.service;

import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.UpdateUserRequest;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserRepresentation> getAll(Pageable pageable);
    Page<UserRepresentation> filter(Pageable pageable,String username);

    UserRepresentation getByUsername(String username);
    UserRepresentation getById(String id);

    void save(CreateUserRequest request);
    UserRepresentation update(String id, UpdateUserRequest request);
    void delete(String id);
    void resetPassword(String id);
    void verifyEmail(String id);

}
