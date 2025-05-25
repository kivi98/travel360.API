package com.travel360.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.model.User;
import com.travel360.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use test profile
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String adminToken;

    @BeforeEach
    public void setup() throws Exception {
        // Clean the database before each test
        userRepository.deleteAll();

        // Register a test admin user
        RegisterRequest adminRequest = new RegisterRequest();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("admin123");
        adminRequest.setFirstName("Admin User");
        adminRequest.setLastName("Admin");
        adminRequest.setEmail("admin@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk());

        // Make the admin user an actual admin in the database
        User adminUser = userRepository.findByUsername("admin").orElseThrow();
        adminUser.setRole(com.travel360.api.model.Role.ADMINISTRATOR);
        userRepository.save(adminUser);

        // Login as admin to get token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        String responseContent = result.andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(responseContent).get("token").asText();
    }

    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void testFullRegistrationAndLoginFlow() throws Exception {
        // Register a new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("user@example.com");
        registerRequest.setFirstName("Test User");
        registerRequest.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login with the new user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    public void testRegisterOperatorAsAdmin() throws Exception {
        // Register an operator using admin privileges
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("operator");
        registerRequest.setPassword("operator123");
        registerRequest.setEmail("operator@example.com");
        registerRequest.setFirstName("Operator User");
        registerRequest.setLastName("User");

        mockMvc.perform(post("/api/auth/register/operator")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login with operator
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("operator");
        loginRequest.setPassword("operator123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("operator")))
                .andExpect(jsonPath("$.role", is("OPERATOR")));
    }

    @Test
    public void testFailedLogin() throws Exception {
        // Try to login with wrong credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistentuser");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
} 