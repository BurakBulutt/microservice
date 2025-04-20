package com.example.serviceusers.users.service;

import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.Page;
import com.example.serviceusers.users.api.UpdateUserRequest;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserService {
    Page<UserRepresentation> getAll(int page, int size);
    Page<UserRepresentation> filter(int page, int size,String username);

    UserRepresentation getByUsername(String username);
    UserRepresentation getById(String id);

    void save(CreateUserRequest request);
    UserRepresentation update(String id, UpdateUserRequest request);
    void delete(String id);
    void resetPassword(String id);
    void verifyEmail(String id);

}
