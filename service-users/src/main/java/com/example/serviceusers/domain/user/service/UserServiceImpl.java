package com.example.serviceusers.domain.user.service;

import com.example.serviceusers.domain.user.api.ChangePasswordRequest;
import com.example.serviceusers.domain.user.api.CreateUserRequest;
import com.example.serviceusers.domain.user.api.UpdateProfileRequest;
import com.example.serviceusers.domain.user.api.UpdateUserRequest;
import com.example.serviceusers.utilities.exception.BaseException;
import com.example.serviceusers.utilities.exception.MessageResource;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.example.serviceusers.domain.user.constants.UserConstants;


@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = UserConstants.CACHE_NAME_USER)
public class UserServiceImpl implements UserService {
    private final StreamBridge streamBridge;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-admin}")
    private String adminClient;

    private UsersResource usersResource;

    @PostConstruct
    public void init() {
        usersResource = keycloak.realm(realm).users();
    }

    @PreDestroy
    public void destroy() {
        keycloak.close();
    }

    @Override
    @Cacheable(value = UserConstants.CACHE_NAME_USER_PAGE,key = "'user-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<UserRepresentation> getAll(Pageable pageable) {
        int first = pageable.getPageNumber() * pageable.getPageSize();
        int max = pageable.getPageSize();
        List<UserRepresentation> users = usersResource.list(first,max);
        int userCount = usersResource.count();
        log.info("Getting all users");
        return new PageImpl<>(users, pageable, userCount);
    }

    @Override
    @Cacheable(value = UserConstants.CACHE_NAME_USER_PAGE,key = "'user-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()",condition = "#username == null")
    public Page<UserRepresentation> filter(Pageable pageable,String username) {
        int first = pageable.getPageNumber() * pageable.getPageSize();
        int max = pageable.getPageSize();
        List<UserRepresentation> users = usersResource.search(username,first,max);
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
                .orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserRepresentation.class.getSimpleName(),username));
    }

    @Override
    public Integer count() {
        return usersResource.count();
    }

    @Override
    @Caching(
            put = {
                    @CachePut(key = "'user-id:' + #result.id"),
                    @CachePut(key = "'user-username:' + #result.username")
            },
            evict = @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true)
    )
    public UserRepresentation save(CreateUserRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setEmailVerified(request.emailVerified());
        userRepresentation.setRequiredActions(new ArrayList<>());
        userRepresentation.setEnabled(request.enabled());
        userRepresentation.setGroups(List.of(UserConstants.USER_GROUP));

        if (!userRepresentation.isEmailVerified()) {
            userRepresentation.getRequiredActions().add(UserConstants.ACTION_VERIFY_EMAIL);
        }

        Map<String, List<String>> attributes = Map.of(UserConstants.ATTRIBUTE_LOCALE, List.of(UserConstants.LOCALE_TR),
                UserConstants.ATTRIBUTE_BIRTHDATE, List.of(request.birthdate().toString()));
        userRepresentation.setAttributes(attributes);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(request.isPasswordTemporary());
        credentialRepresentation.setValue(request.password());
        credentialRepresentation.setType(UserConstants.CREDENTIALS_TYPE_PASSWORD);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        log.warn("Saving user: {}",request);
        Response response = usersResource.create(userRepresentation);

        if (response.getStatus() >= 400) {
            throw new WebApplicationException(response);
        }

        return usersResource.searchByUsername(userRepresentation.getUsername(), Boolean.TRUE).stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserRepresentation.class.getSimpleName(),userRepresentation.getUsername()));
    }

    @Override
    @Caching(
            put = {
                    @CachePut(key = "'user-id:' + #id"),
                    @CachePut(key = "'user-username:' + #result.username")
            },
            evict = @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true)
    )
    public UserRepresentation update(String id, UpdateUserRequest request) {
        UserResource userResource = usersResource.get(id);

        checkRoleForAdminEdit(userResource);

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmailVerified(request.emailVerified());
        userRepresentation.setEnabled(request.enabled());
        userRepresentation.setAttributes(Map.of(UserConstants.ATTRIBUTE_LOCALE, List.of(UserConstants.LOCALE_TR),
                UserConstants.ATTRIBUTE_BIRTHDATE, List.of(request.birthdate().toString())));

        if (!userRepresentation.isEmailVerified() || !request.email().equals(userRepresentation.getEmail())) {
            userRepresentation.getRequiredActions().add(UserConstants.ACTION_VERIFY_EMAIL);
        }

        userRepresentation.setEmail(request.email());

        log.warn("Updating user: {}, updated: {}",id,request);
        userResource.update(userRepresentation);

        return usersResource.get(id).toRepresentation();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true),
            @CacheEvict(key = "'user-id:' + #id")
    })
    public void delete(String id) {
        UserResource userResource = usersResource.get(id);

        checkRoleForAdminEdit(userResource);

        log.warn("Deleting user: {}",id);
        userResource.remove();
        boolean deleteComments = streamBridge.send("deleteUserComments-out-0",id);
        log.info("Sending delete user comments message: {}, status: {}",id,deleteComments);
    }

    @Override
    public void resetPassword(String id) {
        UserResource userResource = usersResource.get(id);

        checkRoleForAdminEdit(userResource);

        UserRepresentation representation = userResource.toRepresentation();
        if (representation.getRequiredActions().stream().noneMatch(action -> action.equals(UserConstants.ACTION_UPDATE_PASSWORD))) {
            representation.getRequiredActions().add(UserConstants.ACTION_UPDATE_PASSWORD);
            userResource.update(representation);
        }
        log.warn("Execute password reset action for user: {}",id);
        userResource.executeActionsEmail(List.of(UserConstants.ACTION_UPDATE_PASSWORD));
    }

    @Override
    public void verifyEmail(String id) {
        UserResource userResource = usersResource.get(id);

        checkRoleForAdminEdit(userResource);

        UserRepresentation representation = userResource.toRepresentation();
        if (representation.getRequiredActions().stream().noneMatch(action -> action.equals(UserConstants.ACTION_VERIFY_EMAIL))) {
            representation.setEmailVerified(Boolean.FALSE);
            representation.getRequiredActions().add(UserConstants.ACTION_VERIFY_EMAIL);
            userResource.update(representation);
        }
        log.warn("Execute verify email action for user: {}",id);
        userResource.sendVerifyEmail();
    }

    @Override
    @Caching(
            put = {
                    @CachePut(key = "'user-id:' + #id"),
                    @CachePut(key = "'user-username:' + #result.username")
            },
            evict = @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true)
    )
    public UserRepresentation updateProfile(String id, UpdateProfileRequest request) {
        UserResource userResource = usersResource.get(id);

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setAttributes(Map.of(UserConstants.ATTRIBUTE_LOCALE, List.of("tr"),
                UserConstants.ATTRIBUTE_BIRTHDATE, List.of(request.birthdate().toString())));

        if (!request.email().equals(userRepresentation.getEmail())) {
            userRepresentation.getRequiredActions().add(UserConstants.ACTION_VERIFY_EMAIL);
        }

        userRepresentation.setEmail(request.email());

        log.warn("Updating user profile: {}, updated: {}",id,request);
        userResource.update(userRepresentation);

        return usersResource.get(id).toRepresentation();
    }

    @Override
    public void changePassword(String id, ChangePasswordRequest request) {
        UserResource userResource = usersResource.get(id);
        log.warn("Change password for user: {}",id);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(UserConstants.CREDENTIALS_TYPE_PASSWORD);
        credentialRepresentation.setValue(request.newPassword());
        credentialRepresentation.setTemporary(Boolean.FALSE);

        userResource.resetPassword(credentialRepresentation);
    }

    private void checkRoleForAdminEdit(UserResource userResource) {
        final String clientUuid = keycloak.realm(realm).clients().findByClientId(adminClient).get(0).getId();

        List<RoleRepresentation> roles = userResource.roles().clientLevel(clientUuid).listEffective();

        if (roles.stream().anyMatch(role -> role.getName().equals(UserConstants.ROLE_ADMIN))) {
            throw new ForbiddenException();
        }
    }
}
