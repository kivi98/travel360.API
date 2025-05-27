package com.travel360.api.controller;

import com.travel360.api.dto.auth.LoginRequest;
import com.travel360.api.dto.auth.LoginResponse;
import com.travel360.api.dto.auth.RegisterRequest;
import com.travel360.api.dto.auth.RegisterResponse;
import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.model.Role;
import com.travel360.api.model.User;
import com.travel360.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user credentials and return JWT token"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(
        @Parameter(description = "User login credentials", required = true)
        @Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.authenticateUser(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Authentication failed"));
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register new customer",
        description = "Register a new customer account in the system"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Registration failed - invalid data or user already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(
        @Parameter(description = "User registration details", required = true)
        @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse registerResponse = userService.registerUser(registerRequest, Role.CUSTOMER);
            return ResponseEntity.ok(ApiResponse.success(registerResponse, "Customer registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Registration failed"));
        }
    }
    
    @PostMapping("/register/operator")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
        summary = "Register new operator",
        description = "Register a new operator account in the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Operator registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Registration failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> registerOperator(
        @Parameter(description = "Operator registration details", required = true)
        @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse registerResponse = userService.registerUser(registerRequest, Role.OPERATOR);
            return ResponseEntity.ok(ApiResponse.success(registerResponse, "Operator registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Operator registration failed"));
        }
    }
    
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
        summary = "Register new administrator",
        description = "Register a new administrator account in the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Administrator registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Registration failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> registerAdmin(
        @Parameter(description = "Administrator registration details", required = true)
        @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse registerResponse = userService.registerUser(registerRequest, Role.ADMINISTRATOR);
            return ResponseEntity.ok(ApiResponse.success(registerResponse, "Administrator registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Administrator registration failed"));
        }
    }
}