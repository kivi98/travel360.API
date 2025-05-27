package com.travel360.api.controller;

import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.dto.user.UserResponse;
import com.travel360.api.model.User;
import com.travel360.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@Tag(name = "User Management", description = "Administrative APIs for managing users (Administrator access required)")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieve all users in the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Requires ADMINISTRATOR role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users, "Retrieved " + users.size() + " users"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve users", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user by ID. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<User>> getUserById(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user, "User found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("User not found with ID: " + id)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update user",
        description = "Update an existing user. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid user data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<User>> updateUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Updated user data", required = true)
        @RequestBody User user) {
        return userService.getUserById(id)
                .map(existingUser -> {
                    try {
                        user.setId(id);
                        User updatedUser = userService.updateUser(user);
                        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
                    } catch (Exception e) {
                        return ResponseEntity.badRequest()
                            .body(ApiResponse.<User>error(e.getMessage(), "Failed to update user"));
                    }
                })
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("User not found with ID: " + id)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deactivate user",
        description = "Deactivate a user account. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User deactivated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    try {
                        userService.deactivateUser(id);
                        return ResponseEntity.ok(ApiResponse.<Void>success("User deactivated successfully"));
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                            .body(ApiResponse.<Void>error(e.getMessage(), "Failed to deactivate user"));
                    }
                })
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("User not found with ID: " + id)));
    }
}