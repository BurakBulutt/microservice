package com.example.serviceauth.user.service;

import com.example.serviceauth.user.api.UpdateRequest;
import com.example.serviceauth.user.dto.UserRepresentationDto;
import com.example.serviceauth.user.dto.UserRepresentationMapper;
import com.example.serviceauth.auth.keycloakutils.KeycloakProperties;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${keycloak-util.users-group-id}")
    public String groupId;

    private final KeycloakProperties keycloakProperties;
    private final Keycloak keycloak;

    public List<UserRepresentationDto> getAllUsers(int page, int size) {
        List<UserRepresentation> members = keycloak.realm(keycloakProperties.getRealm()).groups().group(groupId).members(page * size, size);
        if (members != null && !members.isEmpty()) {
            return members.stream().map(UserRepresentationMapper::toDto).toList();
        }

        return Collections.emptyList();
    }

    public UserRepresentationDto getUserByUsername(String username) {
        List<UserRepresentation> userRepresentations = keycloak.realm(keycloakProperties.getRealm()).users().searchByUsername(username, Boolean.TRUE);
        UserRepresentation user = userRepresentations.stream().filter(userRepresentation -> userRepresentation.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserRepresentationMapper.toDto(user);
    }

    public UserRepresentationDto updateUser(String userId, UpdateRequest request) {
        UserRepresentation userRepresentation = keycloak.realm(keycloakProperties.getRealm()).users().get(userId).toRepresentation();
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setAttributes(request.attributes());

        keycloak.realm(keycloakProperties.getRealm()).users().get(userId).update(userRepresentation);

        return UserRepresentationMapper.toDto(userRepresentation);
    }

    public void deleteUser(String id) {
        keycloak.realm(keycloakProperties.getRealm()).users().get(id).leaveGroup(groupId);
        Response response = keycloak.realm(keycloakProperties.getRealm()).users().delete(id);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new RuntimeException(response.getStatusInfo().getReasonPhrase());
        }
        response.close();
    }
}
