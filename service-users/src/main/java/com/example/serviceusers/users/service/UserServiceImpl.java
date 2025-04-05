package com.example.serviceusers.users.service;

import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.Page;
import com.example.serviceusers.users.api.PageUtil;
import com.example.serviceusers.users.api.UpdateUserRequest;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.*;

import com.example.serviceusers.users.constants.UserServiceConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloakAdmin;
    private final StreamBridge streamBridge;

    @Override
    public Page<UserRepresentation> getAllUsers(int page, int size) {
        int first = page * size;
        final String groupId = getUserGroup().getId();
        int userCount = getUserCount(groupId);
        int totalPages = (int) Math.ceil((double) userCount / size);
        List<UserRepresentation> users = keycloakAdmin.realm(realm)
                .groups()
                .group(groupId)
                .members(first, size);
        log.info("Getting all users");
        return new Page<>(users, new PageUtil(page, size, userCount, totalPages));
    }

    @Override
    public UserRepresentation getUserByUsername(String username) {
        log.info("Getting user by username: {}",username);
        return keycloakAdmin.realm(realm).users().searchByUsername(username, Boolean.TRUE).stream()
                .findFirst()
                .orElseThrow();
    }

    @Override
    @Retry(name = "userRetry")
    public UserRepresentation getUserById(String id) {
        log.info("Getting user: {}",id);
        UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
        return userResource.toRepresentation();
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
        userRepresentation.setGroups(List.of(UserServiceConstants.USER_GROUP));

        if (!userRepresentation.isEmailVerified()) {
            userRepresentation.getRequiredActions().add(UserServiceConstants.ACTION_VERIFY_EMAIL);
        }

        Map<String, List<String>> attributes = Map.of(UserServiceConstants.ATTRIBUTE_LOCALE, List.of("tr"),
                UserServiceConstants.ATTRIBUTE_BIRTHDATE, List.of(request.birthdate()));
        userRepresentation.setAttributes(attributes);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(request.isPasswordTemporary());
        credentialRepresentation.setValue(request.password());
        credentialRepresentation.setType(UserServiceConstants.CREDENTIALS_TYPE_PASSWORD);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        log.warn("Saving user: {}",request);
        Response response = keycloakAdmin.realm(realm).users().create(userRepresentation);

        if (response.getStatus() >= 400) {
            throw new WebApplicationException(response);
        }
    }

    @Override
    public void update(String id, UpdateUserRequest request) {
        UserResource userResource = keycloakAdmin.realm(realm).users().get(id);

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setEmailVerified(request.emailVerified());
        userRepresentation.setEnabled(request.enabled());
        userRepresentation.setAttributes(Map.of(UserServiceConstants.ATTRIBUTE_LOCALE, List.of("tr"),
                UserServiceConstants.ATTRIBUTE_BIRTHDATE, List.of(request.birthdate())));

        log.warn("Updating user: {}, updated: {}",id,request);
        userResource.update(userRepresentation);
    }

    @Override
    public void delete(String id) {
        UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
        log.warn("Deleting user: {}",id);
        userResource.remove();
        boolean deleteComments = streamBridge.send("deleteUserComments-out-0",id);
        log.info("Deleting user comments message: {}, status: {}",id,deleteComments);
    }

    @Override
    public void resetUserPassword(String id) {
        UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
        UserRepresentation representation = userResource.toRepresentation();
        if (representation.getRequiredActions().stream().noneMatch(action -> action.equals(UserServiceConstants.ACTION_UPDATE_PASSWORD))) {
            representation.getRequiredActions().add(UserServiceConstants.ACTION_UPDATE_PASSWORD);
            userResource.update(representation);
        }
        log.warn("Execute password reset action for user: {}",id);
        userResource.executeActionsEmail(List.of(UserServiceConstants.ACTION_UPDATE_PASSWORD));
    }

    @Override
    public void sendVerifyEmail(String id) {
        UserResource userResource = keycloakAdmin.realm(realm).users().get(id);
        UserRepresentation representation = userResource.toRepresentation();
        if (representation.getRequiredActions().stream().noneMatch(action -> action.equals(UserServiceConstants.ACTION_VERIFY_EMAIL))) {
            representation.setEmailVerified(Boolean.FALSE);
            representation.getRequiredActions().add(UserServiceConstants.ACTION_VERIFY_EMAIL);
            userResource.update(representation);
        }
        log.warn("Execute verify email action for user: {}",id);
        userResource.sendVerifyEmail();
    }

    private int getUserCount(String groupId) {
        return keycloakAdmin.realm(realm).groups().group(groupId).members().size();
    }

    private GroupRepresentation getUserGroup() {
        List<GroupRepresentation> groupRepresentations = keycloakAdmin.realm(realm).groups().groups();
        return groupRepresentations.stream()
                .filter(group -> group.getName().equals(UserServiceConstants.USER_GROUP))
                .findFirst()
                .orElseThrow();
    }
}
