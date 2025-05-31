package com.travel360.api.service;

import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.LoginResponse;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.dto.auth.RegisterResponse;
import com.travel360.api.dto.user.UserResponse;
import com.travel360.api.model.Role;
import com.travel360.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    
    LoginResponse authenticateUser(LoginRequest loginRequest);
    
    RegisterResponse registerUser(RegisterRequest registerRequest, Role role);
    
    List<UserResponse> getAllUsers();
    
    Page<UserResponse> getAllUsers(Pageable pageable, String role, String search, Boolean active);
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    User updateUser(User user);
    
    void deactivateUser(Long id);
    
    boolean usernameExists(String username);
    
    boolean emailExists(String email);
} 