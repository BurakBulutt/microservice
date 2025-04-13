package com.example.serviceusers.users.service;

import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.Page;
import com.example.serviceusers.users.api.UpdateUserRequest;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserService {
    Page<UserRepresentation> getAll(int page, int size,String username);

    UserRepresentation getUserByUsername(String username);
    UserRepresentation getUserById(String id);

    void save(CreateUserRequest request);
    void update(String id, UpdateUserRequest request);
    void delete(String id);
    void resetUserPassword(String id);
    void sendVerifyEmail(String id);

}
