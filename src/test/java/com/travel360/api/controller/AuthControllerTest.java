package com.travel360.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.LoginResponse;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.model.Role;
import com.travel360.api.model.User;
import com.travel360.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureWebMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    public void testLoginSuccess() throws Exception {
        // Prepare test data
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        LoginResponse loginResponse = new LoginResponse(
                "test-jwt-token",
                1L,
                "testuser",
                "Test User",
                Role.CUSTOMER
        );

        // Mock service behavior
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn(loginResponse);

        // Perform the test
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    public void testRegisterUserSuccess() throws Exception {
        // Prepare test data
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setFullName("New User");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("newuser");
        mockUser.setRole(Role.CUSTOMER);

        // Mock service behavior
        when(userService.registerUser(any(RegisterRequest.class), eq(Role.CUSTOMER))).thenReturn(mockUser);

        // Perform the test
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testRegisterOperatorWithAdminRole() throws Exception {
        // Prepare test data
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newoperator");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("operator@example.com");
        registerRequest.setFullName("New Operator");

        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("newoperator");
        mockUser.setRole(Role.OPERATOR);

        // Mock service behavior
        when(userService.registerUser(any(RegisterRequest.class), eq(Role.OPERATOR))).thenReturn(mockUser);

        // Perform the test
        mockMvc.perform(post("/api/auth/register/operator")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Operator registered successfully"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testRegisterOperatorWithCustomerRole() throws Exception {
        // Prepare test data
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newoperator");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("operator@example.com");
        registerRequest.setFullName("New Operator");

        // Perform the test - should fail due to insufficient permissions
        mockMvc.perform(post("/api/auth/register/operator")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
} 