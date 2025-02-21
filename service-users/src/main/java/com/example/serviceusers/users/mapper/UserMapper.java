package com.example.serviceusers.users.mapper;

import com.example.serviceusers.users.api.UserRepresentationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserRepresentationResponse toUserRepresentationResponse(UserRepresentation userRepresentation) {
        return UserRepresentationResponse.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .emailVerified(userRepresentation.isEmailVerified())
                .attributes(userRepresentation.getAttributes())
                .createdTimestamp(userRepresentation.getCreatedTimestamp())
                .enabled(userRepresentation.isEnabled())
                .federationLink(userRepresentation.getFederationLink())
                .serviceAccountClientId(userRepresentation.getServiceAccountClientId())
                .disableableCredentialTypes(userRepresentation.getDisableableCredentialTypes())
                .requiredActions(userRepresentation.getRequiredActions())
                .federatedIdentities(userRepresentation.getFederatedIdentities())
                .realmRoles(userRepresentation.getRealmRoles())
                .clientRoles(userRepresentation.getClientRoles())
                .clientConsents(userRepresentation.getClientConsents())
                .notBefore(userRepresentation.getNotBefore())
                .groups(userRepresentation.getGroups())
                .access(userRepresentation.getAccess())
                .build();
    }

    public static List<UserRepresentationResponse> toUserRepresentationResponses(List<UserRepresentation> userRepresentations) {
        return userRepresentations.stream().map(UserMapper::toUserRepresentationResponse).toList();
    }
}
