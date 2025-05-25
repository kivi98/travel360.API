package com.travel360.api.controller;

import com.travel360.api.config.CorsConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
@Tag(name = "System Information", description = "System configuration and debugging endpoints")
public class CorsInfoController {

    @Autowired
    private CorsConfig corsConfig;

    @GetMapping("/cors-info")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Get CORS configuration",
        description = "Returns current CORS configuration for debugging purposes. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "CORS configuration retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CorsConfig.CorsConfigurationInfo.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions (requires OPERATOR or ADMINISTRATOR role)",
            content = @Content
        )
    })
    public ResponseEntity<CorsConfig.CorsConfigurationInfo> getCorsInfo() {
        return ResponseEntity.ok(corsConfig.getCorsInfo());
    }
} 