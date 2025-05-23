package com.example.serviceusers.users.api;

import com.example.serviceusers.domain.user.api.UserController;
import com.example.serviceusers.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getAll_shouldReturnUserRepresentationPage() throws Exception {
        // Arrange
        UserRepresentation user1 = new UserRepresentation();
        user1.setId("1");
        UserRepresentation user2 = new UserRepresentation();
        user2.setId("2");

        Pageable pageable = PageRequest.of(0, 10);
        int totalElements = 2;
        Page<UserRepresentation> page = new PageImpl<>(List.of(user1,user2),pageable,totalElements);

        when(userService.getAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));
    }
}
