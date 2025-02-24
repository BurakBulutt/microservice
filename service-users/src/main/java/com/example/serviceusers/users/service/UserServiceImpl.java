package com.example.serviceusers.users.service;

import com.example.serviceusers.rest.BaseException;
import com.example.serviceusers.rest.MessageResource;
import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.UpdateUserRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloakAdmin;

    @Override
    public List<UserRepresentation> getAllUsers(int page, int size) {
        int first = page * size;
        return keycloakAdmin.realm(realm).users().list(first, size).stream()
                .filter(userRepresentation -> checkRoles(userRepresentation.getId()))
                .toList();
    }

    @Override
    public UserRepresentation getUserByUsername(String username) {
        return keycloakAdmin.realm(realm).users().search(username).stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,username));
    }

    @Override
    public void save(CreateUserRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setEmailVerified(request.emailVerified());
        userRepresentation.setRequiredActions(new ArrayList<>());
        userRepresentation.setEnabled(request.enabled());

        if (!userRepresentation.isEmailVerified()) {
            userRepresentation.getRequiredActions().add("VERIFY_EMAIL");
        }

        Map<String,List<String>> attributes = Map.of("locale", List.of("tr"),"birthdate",List.of(request.birthdate()));
        userRepresentation.setAttributes(attributes);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(request.isPasswordTemporary());
        credentialRepresentation.setValue(request.password());
        credentialRepresentation.setType("password");
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        Response response = keycloakAdmin.realm(realm).users().create(userRepresentation);

        switch (response.getStatus()) {
            case 201 -> {
                return;
            }
            case 409 -> {
                throw new BaseException(MessageResource.CONFLICT);
            }
            case 400 -> {
                throw new BaseException(MessageResource.BAD_REQUEST);
            }
        }
    }

    @Override
    public void update(String id, UpdateUserRequest request) {
        try {
        UserResource userResource = keycloakAdmin.realm(realm).users().get(id);

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setEmailVerified(request.emailVerified());
        userRepresentation.setEnabled(request.enabled());
        userRepresentation.setAttributes(Map.of("locale", List.of("tr"),"birthdate",List.of(request.birthdate())));

        userResource.update(userRepresentation);
        }catch (BadRequestException e) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,id);
        }
    }

    @Override
    public void delete(String id) {
        try {
            UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
            userResource.remove();
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,id);
        }catch (BadRequestException e) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }
    }

    @Override
    public void resetUserPassword(String id) {
        try {
            UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
            UserRepresentation representation = userResource.toRepresentation();
            if(representation.getRequiredActions().stream().noneMatch(action -> action.equals("UPDATE_PASSWORD"))) {
                representation.getRequiredActions().add("UPDATE_PASSWORD");
                userResource.update(representation);
            }
            userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,id);
        }catch (InternalServerErrorException e) {
            throw new BaseException(MessageResource.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void sendVerifyEmail(String id) {
        try {
            UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
            UserRepresentation representation = userResource.toRepresentation();
            representation.setEmailVerified(Boolean.FALSE);
            representation.getRequiredActions().add("VERIFY_EMAIL");
            userResource.update(representation);
            userResource.sendVerifyEmail();
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,id);
        }catch (InternalServerErrorException e) {
            throw new BaseException(MessageResource.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Integer getUserCount() {
        return Objects.requireNonNull(keycloakAdmin.realm(realm).users().count());
    }

    private boolean checkRoles(String id) {
        UserResource user = keycloakAdmin.realm(realm).users().get(id);
        return user.roles().realmLevel().listAll().stream().noneMatch(role -> role.getName().equals("ADMIN"));
    }
}
