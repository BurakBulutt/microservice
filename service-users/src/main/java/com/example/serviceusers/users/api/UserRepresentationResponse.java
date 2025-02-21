package com.example.serviceusers.users.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.keycloak.json.StringListMapDeserializer;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.UserConsentRepresentation;
import org.keycloak.representations.idm.UserProfileMetadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserRepresentationResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    @JsonDeserialize(
            using = StringListMapDeserializer.class
    )
    private Map<String, List<String>> attributes;
    private UserProfileMetadata userProfileMetadata;
    private String self;
    private String origin;
    private Long createdTimestamp;
    private Boolean enabled;
    private String federationLink;
    private String serviceAccountClientId;
    private Set<String> disableableCredentialTypes;
    private List<String> requiredActions;
    private List<FederatedIdentityRepresentation> federatedIdentities;
    private List<String> realmRoles;
    private Map<String, List<String>> clientRoles;
    private List<UserConsentRepresentation> clientConsents;
    private Integer notBefore;
    private List<String> groups;
    private Map<String, Boolean> access;
}
