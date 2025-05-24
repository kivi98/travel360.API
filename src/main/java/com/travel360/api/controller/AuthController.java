package com.travel360.api.controller;

import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.LoginResponse;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.model.Role;
import com.travel360.api.model.User;
import com.travel360.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest, Role.CUSTOMER);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/register/operator")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> registerOperator(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest, Role.OPERATOR);
            return ResponseEntity.ok("Operator registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest, Role.ADMINISTRATOR);
            return ResponseEntity.ok("Administrator registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}