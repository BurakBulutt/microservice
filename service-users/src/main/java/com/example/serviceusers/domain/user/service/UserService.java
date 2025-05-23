package com.example.serviceusers.domain.user.service;

import com.example.serviceusers.domain.user.api.ChangePasswordRequest;
import com.example.serviceusers.domain.user.api.CreateUserRequest;
import com.example.serviceusers.domain.user.api.UpdateProfileRequest;
import com.example.serviceusers.domain.user.api.UpdateUserRequest;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserRepresentation> getAll(Pageable pageable);
    Page<UserRepresentation> filter(Pageable pageable,String username);

    UserRepresentation getByUsername(String username);
    UserRepresentation getById(String id);

    UserRepresentation save(CreateUserRequest request);
    UserRepresentation update(String id, UpdateUserRequest request);
    UserRepresentation updateProfile(String id, UpdateProfileRequest request);

    void delete(String id);
    void resetPassword(String id);
    void verifyEmail(String id);

    void changePassword(String id, ChangePasswordRequest request);

    Integer count();

}
