package com.example.serviceusers.users.service;

import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.UpdateUserRequest;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.example.serviceusers.users.constants.UserServiceConstants;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "userCache")
public class UserServiceImpl implements UserService {
    private final UsersResource usersResource;
    private final StreamBridge streamBridge;

    @Override
    @Cacheable(value = "userPageCache",key = "'user-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<UserRepresentation> getAll(Pageable pageable) {
        int first = pageable.getPageNumber() * pageable.getPageSize();
        List<UserRepresentation> users = usersResource.list(first,pageable.getPageSize());
        int userCount = usersResource.count();
        log.info("Getting all users");
        return new PageImpl<>(users, pageable, userCount);
    }

    @Override
    @Cacheable(value = "userPageCache",key = "'user-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()",condition = "#username == null")
    public Page<UserRepresentation> filter(Pageable pageable,String username) {
        int first = pageable.getPageNumber() * pageable.getPageSize();
        List<UserRepresentation> users = usersResource.search(username,first,pageable.getPageSize());
        int userCount = usersResource.count(username);
        log.info("Getting filtered users");
        return new PageImpl<>(users, pageable, userCount);
    }

    @Override
    @Retry(name = "userRetry")
    @Cacheable(key = "'user-id:' + #id")
    public UserRepresentation getById(String id) {
        log.info("Getting user: {}",id);
        UserResource userResource = usersResource.get(id);
        return userResource.toRepresentation();
    }

    @Override
    @Cacheable(key = "'user-username:' + #username")
    public UserRepresentation getByUsername(String username) {
        log.info("Getting user by username: {}",username);
        return usersResource.searchByUsername(username, Boolean.TRUE).stream()
                .findFirst()
                .orElseThrow();
    }

    @Override
    public UserRepresentation save(CreateUserRequest request) {
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
        Response response = usersResource.create(userRepresentation);

        if (response.getStatus() >= 400) {
            throw new WebApplicationException(response);
        }

        return usersResource.searchByUsername(userRepresentation.getUsername(), Boolean.TRUE).stream().findFirst().get();
    }

    @Override
    @Caching(
            put = {
                    @CachePut(key = "'user-id:' + #id"),
                    @CachePut(key = "'user-username:' + #result.username")
            },
            evict = @CacheEvict(value = "userPageCache", allEntries = true)
    )
    public UserRepresentation update(String id, UpdateUserRequest request) {
        UserResource userResource = usersResource.get(id);

        if (isAdmin(userResource.roles().realmLevel().listEffective())){
            throw new WebApplicationException("Can not update admin",Response.status(Response.Status.FORBIDDEN).build());
        }

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

        return usersResource.get(id).toRepresentation();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userPageCache", allEntries = true),
            @CacheEvict(key = "'user-id:' + #id")
    })
    public void delete(String id) {
        UserResource userResource = usersResource.get(id);

        if (isAdmin(userResource.roles().realmLevel().listEffective())){
            throw new WebApplicationException("Can not delete admin",Response.status(Response.Status.FORBIDDEN).build());
        }

        log.warn("Deleting user: {}",id);
        userResource.remove();
        boolean deleteComments = streamBridge.send("deleteUserComments-out-0",id);
        log.info("Sending delete user comments message: {}, status: {}",id,deleteComments);
    }

    @Override
    public void resetPassword(String id) {
        UserResource userResource = usersResource.get(id);

        if (isAdmin(userResource.roles().realmLevel().listEffective())){
            throw new WebApplicationException("Can not update admin",Response.status(Response.Status.FORBIDDEN).build());
        }

        UserRepresentation representation = userResource.toRepresentation();
        if (representation.getRequiredActions().stream().noneMatch(action -> action.equals(UserServiceConstants.ACTION_UPDATE_PASSWORD))) {
            representation.getRequiredActions().add(UserServiceConstants.ACTION_UPDATE_PASSWORD);
            userResource.update(representation);
        }
        log.warn("Execute password reset action for user: {}",id);
        userResource.executeActionsEmail(List.of(UserServiceConstants.ACTION_UPDATE_PASSWORD));
    }

    @Override
    public void verifyEmail(String id) {
        UserResource userResource = usersResource.get(id);

        if (isAdmin(userResource.roles().realmLevel().listEffective())){
            throw new WebApplicationException("Can not update admin",Response.status(Response.Status.FORBIDDEN).build());
        }

        UserRepresentation representation = userResource.toRepresentation();
        if (representation.getRequiredActions().stream().noneMatch(action -> action.equals(UserServiceConstants.ACTION_VERIFY_EMAIL))) {
            representation.setEmailVerified(Boolean.FALSE);
            representation.getRequiredActions().add(UserServiceConstants.ACTION_VERIFY_EMAIL);
            userResource.update(representation);
        }
        log.warn("Execute verify email action for user: {}",id);
        userResource.sendVerifyEmail();
    }

    private boolean isAdmin(List<RoleRepresentation> realmRoles) {
        return realmRoles.stream().filter(role -> !role.getClientRole())
                .map(RoleRepresentation::getName)
                .anyMatch(role -> role.equals(UserServiceConstants.ROLE_ADMIN));
    }
}
