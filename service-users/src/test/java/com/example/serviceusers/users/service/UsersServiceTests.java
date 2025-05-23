package com.example.serviceusers.users.service;


import com.example.serviceusers.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTests {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UsersResource usersResource;


    @Test
    void getAll_shouldReturnUserRepresentationPage() {
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

        int first = pageable.getPageNumber() * pageable.getPageSize();
        int max = pageable.getPageSize();

        when(usersResource.list(first, max)).thenReturn(mockUsers);
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
}
