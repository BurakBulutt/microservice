package com.example.serviceusers.users.mapper;

import com.example.serviceusers.users.api.Page;
import com.example.serviceusers.users.api.UserRepresentationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserRepresentationResponse toUserRepresentationResponse(UserRepresentation userRepresentation)  {
        String birthDateStr = Objects.requireNonNull(userRepresentation.getAttributes().get("birthdate").get(0));

        return UserRepresentationResponse.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .emailVerified(userRepresentation.isEmailVerified())
                .birthdate(birthDateStr)
                .createdTimestamp(userRepresentation.getCreatedTimestamp())
                .enabled(userRepresentation.isEnabled())
                .build();
    }

    public static List<UserRepresentationResponse> toUserRepresentationResponses(List<UserRepresentation> userRepresentations) {
        return userRepresentations.stream().map(UserMapper::toUserRepresentationResponse).toList();
    }

    public static Page<UserRepresentationResponse> toPageResponse(Page<UserRepresentation> userRepresentationPage){
        return userRepresentationPage.map(UserMapper::toUserRepresentationResponse);
    }
}
