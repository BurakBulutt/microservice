package com.example.serviceusers.domain.user.mapper;

import com.example.serviceusers.domain.user.api.UserRepresentationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserApiMapper {

    public static UserRepresentationResponse toUserRepresentationResponse(UserRepresentation userRepresentation)  {
        LocalDate birthDate = null;

        if (userRepresentation.getAttributes() != null && !userRepresentation.getAttributes().isEmpty()) {
            List<String> result = userRepresentation.getAttributes().get("birthdate");

            if (result != null && !result.isEmpty()) {
                birthDate = LocalDate.parse(result.get(0), DateTimeFormatter.ISO_DATE);
            }
        }

        return UserRepresentationResponse.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .emailVerified(userRepresentation.isEmailVerified())
                .birthdate(birthDate)
                .createdTimestamp(userRepresentation.getCreatedTimestamp())
                .enabled(userRepresentation.isEnabled())
                .build();
    }

    public static Page<UserRepresentationResponse> toPageResponse(Page<UserRepresentation> userRepresentationPage){
        return userRepresentationPage.map(UserApiMapper::toUserRepresentationResponse);
    }
}
