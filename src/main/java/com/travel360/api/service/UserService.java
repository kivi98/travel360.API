package com.travel360.api.service;

import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.LoginResponse;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.model.Role;
import com.travel360.api.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    
    LoginResponse authenticateUser(LoginRequest loginRequest);
    
    User registerUser(RegisterRequest registerRequest, Role role);
    
    List<User> getAllUsers();
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    User updateUser(User user);
    
    void deactivateUser(Long id);
    
    boolean usernameExists(String username);
    
    boolean emailExists(String email);
} 