package com.example.serviceauth.user.dto;

import com.example.serviceauth.auth.api.TokenResponse;
import com.example.serviceauth.user.api.UserRepresentationResponse;
import com.example.serviceauth.user.api.RegisterRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRepresentationMapper {

    public static UserRepresentationDto toDto(UserRepresentation userRepresentation) {
        UserRepresentationDto userRepresentationDto = UserRepresentationDto.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .enabled(userRepresentation.isEnabled())
                .emailVerified(userRepresentation.isEmailVerified())
                .attributes(userRepresentation.getAttributes())
                .build();
        if (userRepresentation.getCredentials() != null && !userRepresentation.getCredentials().isEmpty()) {
            userRepresentationDto.setPassword(userRepresentation.getCredentials().get(0).getValue());
        }

        return userRepresentationDto;
    }

    public static UserRepresentationDto toDto(RegisterRequest request) {
        Map<String,List<String>> attributes = new HashMap<>();
        attributes.put("birthdate",List.of(request.birthdate()));
        attributes.put("locale",List.of("tr"));

        return UserRepresentationDto.builder()
                .firstName(request.name())
                .lastName(request.surname())
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .attributes(attributes)
                .build();
    }

    public static UserRepresentationResponse toResponse(UserRepresentationDto userRepresentationDto) {
        return UserRepresentationResponse.builder()
                .firstName(userRepresentationDto.getFirstName())
                .lastName(userRepresentationDto.getLastName())
                .username(userRepresentationDto.getUsername())
                .email(userRepresentationDto.getEmail())
                .attributes(userRepresentationDto.getAttributes())
                .build();
    }

    public static List<UserRepresentationResponse> toResponseList(List<UserRepresentationDto> userRepresentationDtoList) {
        return userRepresentationDtoList.stream().map(UserRepresentationMapper::toResponse).collect(Collectors.toList());
    }

    public static TokenResponse toTokenResponse(AccessTokenResponse tokenResponse) {
        return TokenResponse.builder()
                .accessToken(tokenResponse.getToken())
                .expiresIn(tokenResponse.getExpiresIn())
                .refreshToken(tokenResponse.getRefreshToken())
                .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                .build();
    }
}
