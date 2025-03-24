package com.example.serviceusers.users.service;

import com.example.serviceusers.keycloak.config.KeycloakConfigProperties;
import com.example.serviceusers.rest.BaseException;
import com.example.serviceusers.rest.MessageResource;
import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.Page;
import com.example.serviceusers.users.api.PageUtil;
import com.example.serviceusers.users.api.UpdateUserRequest;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String userGroup = "app-users-group";

    private final Keycloak keycloakAdmin;
    private final KeycloakConfigProperties keycloakConfig;

    @Override
    public Page<UserRepresentation> getAllUsers(int page, int size) {
        int first = page * size;
        int userCount = getUserCount();
        int totalPages = (int) Math.ceil((double) userCount /size);
        List<UserRepresentation> users = keycloakAdmin.realm(keycloakConfig.getRealm()).groups().group(keycloakConfig.getUserGroup()).members(first,size);
        return new Page<>(users,new PageUtil(page,size,userCount,totalPages));
    }

    @Override
    public UserRepresentation getUserByUsername(String username) {
        return keycloakAdmin.realm(keycloakConfig.getRealm()).users().search(username).stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,UserRepresentation.class.getSimpleName(),username));
    }

    @Retry(name = "userRetry")
    @Override
    public UserRepresentation getUserById(String id) {
        try{
            return keycloakAdmin.realm(keycloakConfig.getRealm()).users().get(id).toRepresentation();
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,UserRepresentation.class.getSimpleName(),id);
        }
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
        userRepresentation.setGroups(List.of(userGroup));

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

        Response response = keycloakAdmin.realm(keycloakConfig.getRealm()).users().create(userRepresentation);

        switch (response.getStatus()) {
            case 409 -> throw new BaseException(MessageResource.CONFLICT);
            case 400 -> throw new BaseException(MessageResource.BAD_REQUEST);
        }
    }

    @Override
    public void update(String id, UpdateUserRequest request) {
        try {
        UserResource userResource = keycloakAdmin.realm(keycloakConfig.getRealm()).users().get(id);

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
            throw new BaseException(MessageResource.NOT_FOUND,UserResource.class.getSimpleName(),id);
        }catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 409) {
                throw new BaseException(MessageResource.CONFLICT);
            }
            log.warn("Response Status -> {}, Message -> {}", e.getResponse().getStatus(),e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            UserResource userResource = keycloakAdmin.realm(keycloakConfig.getRealm()).users().get(id);
            userResource.remove(); //TODO MESSAGE BROKER ILE SAGA AKISI KURULMALIDIR.
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,UserResource.class.getSimpleName(),id);
        }catch (BadRequestException e) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }
    }

    @Override
    public void resetUserPassword(String id) {
        try {
            UserResource userResource = keycloakAdmin.realm(keycloakConfig.getRealm()).users().get(id);
            UserRepresentation representation = userResource.toRepresentation();
            if(representation.getRequiredActions().stream().noneMatch(action -> action.equals("UPDATE_PASSWORD"))) {
                representation.getRequiredActions().add("UPDATE_PASSWORD");
                userResource.update(representation);
            }
            userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,UserResource.class.getSimpleName(),id);
        }catch (InternalServerErrorException e) {
            log.error(e.getMessage());
            throw new BaseException(MessageResource.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void sendVerifyEmail(String id) {
        try {
            UserResource userResource = keycloakAdmin.realm(keycloakConfig.getRealm()).users().get(id);
            UserRepresentation representation = userResource.toRepresentation();
            representation.setEmailVerified(Boolean.FALSE);
            representation.getRequiredActions().add("VERIFY_EMAIL");
            userResource.sendVerifyEmail();
            userResource.update(representation);
        }catch (NotFoundException e) {
            throw new BaseException(MessageResource.NOT_FOUND,UserResource.class.getSimpleName(),id);
        }catch (InternalServerErrorException e) {
            log.error(e.getMessage());
            throw new BaseException(MessageResource.INTERNAL_SERVER_ERROR);
        }
    }

    private int getUserCount() {
        return keycloakAdmin.realm(keycloakConfig.getRealm()).groups().group(keycloakConfig.getUserGroup()).members().size();
    }
}
