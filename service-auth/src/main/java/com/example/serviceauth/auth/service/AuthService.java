package com.example.serviceauth.auth.service;

import com.example.serviceauth.auth.keycloakutils.feign.KeylocakFeignClient;
import com.example.serviceauth.auth.api.LoginRequest;
import com.example.serviceauth.user.dto.UserRepresentationDto;
import com.example.serviceauth.user.dto.UserRepresentationMapper;
import com.example.serviceauth.auth.keycloakutils.*;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String USER_GROUP = "microservice-users-group";
    private static final String REDIRECT_URI = "http://localhost:8081/auth/callback";

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
    private final KeylocakFeignClient feignClient;

    public AccessTokenResponse login(LoginRequest request) {
        Map<String, String> form = generateForm(OAuth2Constants.PASSWORD);
        form.put(OAuth2Constants.USERNAME, request.username());
        form.put(OAuth2Constants.PASSWORD, request.password());

        return feignClient.getAccessToken(keycloakProperties.getRealm(), form);
    }

    public AccessTokenResponse loginWithAuthorizationCode(String authorizationCode) {
        Map<String,String> form = generateForm(OAuth2Constants.AUTHORIZATION_CODE);
        form.put(OAuth2Constants.CODE,authorizationCode);
        form.put(OAuth2Constants.REDIRECT_URI,REDIRECT_URI);

        return feignClient.getAccessToken(keycloakProperties.getRealm(), form);
    }

    public void logout() {
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            final String userId = authentication.getName();
            keycloak.realm(keycloakProperties.getRealm()).users().get(userId).logout();
            return;
        }
        throw new RuntimeException("Logout failed");
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        Map<String, String> form = generateForm(OAuth2Constants.REFRESH_TOKEN);
        form.put(OAuth2Constants.REFRESH_TOKEN, refreshToken);

        return feignClient.getAccessToken(keycloakProperties.getRealm(), form);
    }

    public UserRepresentationDto register(UserRepresentationDto userRepresentationDto) {
        UserRepresentation userRepresentation = createUserPresentation(userRepresentationDto);
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(Boolean.FALSE);
        userRepresentation.setGroups(Collections.singletonList(USER_GROUP));

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRepresentationDto.getPassword());
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setTemporary(Boolean.FALSE);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        Response response = keycloak.realm(keycloakProperties.getRealm()).users().create(userRepresentation);

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            List<UserRepresentation> savedUserList = keycloak.realm(keycloakProperties.getRealm()).users().searchByUsername(userRepresentation.getUsername(), Boolean.TRUE);
            UserRepresentation savedUser = savedUserList.stream().filter(userPresentation1 -> userPresentation1.getUsername().equals(userRepresentation.getUsername()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("User not found"));
            response.close();
            return UserRepresentationMapper.toDto(savedUser);
        }
        throw new RuntimeException(response.getStatusInfo().getReasonPhrase());
    }

    private UserRepresentation createUserPresentation(UserRepresentationDto userRepresentationDto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setCreatedTimestamp(System.currentTimeMillis());
        userRepresentation.setFirstName(userRepresentationDto.getFirstName());
        userRepresentation.setLastName(userRepresentationDto.getLastName());
        userRepresentation.setUsername(userRepresentationDto.getUsername());
        userRepresentation.setEmail(userRepresentationDto.getEmail());
        userRepresentation.setAttributes(userRepresentationDto.getAttributes());

        return userRepresentation;
    }

    private Map<String ,String> generateForm(String grantType) {
        Map<String, String> form = new HashMap<>();
        form.put(OAuth2Constants.GRANT_TYPE, grantType);
        form.put(OAuth2Constants.CLIENT_ID, keycloakProperties.getResource());
        form.put(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getCredentials().getSecret());

        return form;
    }
}
