package com.example.serviceusers.users.service;


import com.example.serviceusers.users.api.CreateUserRequest;
import com.example.serviceusers.users.api.UpdateUserRequest;
import com.example.serviceusers.users.constants.UserServiceConstants;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTests {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UsersResource usersResource;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private UserResource userResource;

    @Test
    void getAll_shouldReturnUserRepresentationPage() {
        // Arrange
        UserRepresentation user1 = new UserRepresentation();
        user1.setId("1");
        user1.setUsername("Selam");
        UserRepresentation user2 = new UserRepresentation();
        user2.setId("2");
        user2.setUsername("Solam");

        Pageable pageable = PageRequest.of(0, 10);
        List<UserRepresentation> mockUsers = List.of(user1, user2);
        int totalUsers = 2;

        when(usersResource.list(0, 10)).thenReturn(mockUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // Act
        Page<UserRepresentation> result = service.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(mockUsers, result.getContent());
        assertEquals(mockUsers.size(), result.getContent().size());
        assertEquals(totalUsers, result.getTotalElements());

        verify(usersResource).list(0, 10);
        verify(usersResource).count();
    }

    @Test
    void filter_shouldReturnUserRepresentationPage() {
        // Arrange
        UserRepresentation user1 = new UserRepresentation();
        user1.setId("1");
        user1.setUsername("user1");
        UserRepresentation user2 = new UserRepresentation();
        user2.setId("2");
        user2.setUsername("user2");

        Pageable pageable = PageRequest.of(0, 10);
        List<UserRepresentation> mockUsers = List.of(user1, user2);
        int totalUsers = 2;
        String username = "user";

        when(usersResource.search(username,0, 10)).thenReturn(mockUsers);
        when(usersResource.count(username)).thenReturn(totalUsers);

        // Act
        Page<UserRepresentation> result = service.filter(pageable,username);

        // Assert
        assertNotNull(result);
        assertEquals(mockUsers, result.getContent());
        assertEquals(mockUsers.size(), result.getContent().size());
        assertEquals(totalUsers, result.getTotalElements());

        verify(usersResource).search(username,0, 10);
        verify(usersResource).count(username);
    }

    @Test
    void getById_shouldReturnUserRepresentation_whenIdExists() {
        // Arrange
        String userId = "123";
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setId(userId);

        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(expectedUser);

        // Act
        UserRepresentation result = service.getById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(usersResource).get(userId);
        verify(userResource).toRepresentation();
    }

    @Test
    void getById_shouldReturnUserRepresentation_whenIdNotFound() {
        // Arrange
        String userId = "123";

        when(usersResource.get(userId)).thenThrow(new NotFoundException());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getById(userId));

        verify(usersResource).get(userId);
    }

    @Test
    void getByUsername_shouldReturnUserRepresentation_whenUserExists() {
        // Arrange
        String username = "exists";
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setUsername(username);

        when(usersResource.searchByUsername(username, true)).thenReturn(List.of(expectedUser));

        // Act
        UserRepresentation result = service.getByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());

        verify(usersResource).searchByUsername(username, true);
    }

    @Test
    void getByUsername_shouldThrowException_whenUserNotFound() {
        // Arrange
        String username = "nonexistent";
        when(usersResource.searchByUsername(username, true)).thenReturn(List.of());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> service.getByUsername(username));

        verify(usersResource).searchByUsername(username, true);
    }

    @Test
    void save_shouldCreateUserSuccessfully() {
        // Arrange
        CreateUserRequest createUserRequest = Mockito.mock(CreateUserRequest.class);

        when(createUserRequest.username()).thenReturn("user");
        when(createUserRequest.firstName()).thenReturn("Test");
        when(createUserRequest.lastName()).thenReturn("User");
        when(createUserRequest.email()).thenReturn("test@example.com");
        when(createUserRequest.emailVerified()).thenReturn(true);
        when(createUserRequest.enabled()).thenReturn(true);
        when(createUserRequest.birthdate()).thenReturn("1990-01-01");
        when(createUserRequest.isPasswordTemporary()).thenReturn(false);
        when(createUserRequest.password()).thenReturn("securepassword");

        when(usersResource.create(Mockito.any(UserRepresentation.class))).thenReturn(Response.ok().build());

        // Act
        service.save(createUserRequest);

        // Assert
        verify(usersResource).create(Mockito.any(UserRepresentation.class));
    }

    @Test
    void update_shouldUpdateUserSuccessfully() {
        // Arrange
        String userId = "abc123";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        UpdateUserRequest updateUserRequest = Mockito.mock(UpdateUserRequest.class);

        when(updateUserRequest.firstName()).thenReturn("Updated");
        when(updateUserRequest.lastName()).thenReturn("User");
        when(updateUserRequest.email()).thenReturn("updated@example.com");
        when(updateUserRequest.emailVerified()).thenReturn(true);
        when(updateUserRequest.enabled()).thenReturn(true);
        when(updateUserRequest.birthdate()).thenReturn("1990-01-01");

        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(userResource.roles()).thenReturn(Mockito.mock(RoleMappingResource.class));
        when(userResource.roles().realmLevel()).thenReturn(Mockito.mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listEffective()).thenReturn(List.of());

        // Act
        service.update(userId, updateUserRequest);

        // Assert
        verify(userResource).update(Mockito.any(UserRepresentation.class));
    }

    @Test
    void delete_shouldDeleteUserSuccessfully() {
        // Arrange
        String userId = "delete123";

        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.roles()).thenReturn(Mockito.mock(RoleMappingResource.class));
        when(userResource.roles().realmLevel()).thenReturn(Mockito.mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listEffective()).thenReturn(List.of());
        when(streamBridge.send("deleteUserComments-out-0", userId)).thenReturn(true);

        // Act
        service.delete(userId);

        // Assert
        verify(userResource).remove();
    }

    @Test
    void resetPassword_shouldAddActionAndSendEmail() {
        // Arrange
        String userId = "reset123";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setRequiredActions(new ArrayList<>());

        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(userResource.roles()).thenReturn(Mockito.mock(RoleMappingResource.class));
        when(userResource.roles().realmLevel()).thenReturn(Mockito.mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listEffective()).thenReturn(List.of());

        // Act
        service.resetPassword(userId);

        // Assert
        verify(userResource).update(Mockito.any(UserRepresentation.class));
        verify(userResource).executeActionsEmail(List.of(UserServiceConstants.ACTION_UPDATE_PASSWORD));
    }

    @Test
    void verifyEmail_shouldAddActionAndSendEmail() {
        // Arrange
        String userId = "verify123";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setRequiredActions(new ArrayList<>());

        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(userResource.roles()).thenReturn(Mockito.mock(RoleMappingResource.class));
        when(userResource.roles().realmLevel()).thenReturn(Mockito.mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listEffective()).thenReturn(List.of());

        // Act
        service.verifyEmail(userId);

        // Assert
        verify(userResource).update(Mockito.any(UserRepresentation.class));
        verify(userResource).sendVerifyEmail();
    }

}
