package com.travel360.api.service.impl;

import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.LoginResponse;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.dto.auth.RegisterResponse;
import com.travel360.api.dto.user.UserResponse;
import com.travel360.api.model.Role;
import com.travel360.api.model.User;
import com.travel360.api.repository.UserRepository;
import com.travel360.api.security.JwtTokenProvider;
import com.travel360.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        
        return new LoginResponse(
            jwt, 
            user.getId(), 
            user.getUsername(), 
            user.getFirstName(), 
            user.getLastName(), 
            user.getEmail(),
            user.getRole());
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest, Role role) {
        if (usernameExists(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        
        if (emailExists(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setRole(role);
        
        User savedUser = userRepository.save(user);
        
        // Create and return RegisterResponse
        RegisterResponse response = new RegisterResponse();
        response.setUserId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setRole(savedUser.getRole());
        
        return response;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole() != null ? user.getRole().name() : null,
                        user.isActive(),
                        user.getCreatedAt() != null ? Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null,
                        user.getUpdatedAt() != null ? Date.from(user.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null
                ))
                .toList();
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, String role, String search, Boolean active) {
        Page<User> userPage = userRepository.findAll(pageable, role, search, active);
        return userPage.map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole() != null ? user.getRole().name() : null,
                user.isActive(),
                user.getCreatedAt() != null ? Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null,
                user.getUpdatedAt() != null ? Date.from(user.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()) : null
        ));
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
} 