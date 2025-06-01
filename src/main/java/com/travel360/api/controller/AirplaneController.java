package com.travel360.api.controller;

import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.dto.common.Pagination;
import com.travel360.api.dto.flight.AirplaneDto;
import com.travel360.api.model.Airplane;
import com.travel360.api.model.AirplaneSize;
import com.travel360.api.service.AirplaneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airplanes")
@Tag(name = "Airplane Management", description = "APIs for managing airplanes")
public class AirplaneController {

    @Autowired
    private AirplaneService airplaneService;

    @GetMapping
    @Operation(
        summary = "Get all airplanes",
        description = "Retrieve all airplanes with optional pagination, search, and filtering functionality",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplanes retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirplaneDto>>> getAllAirplanes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AirplaneSize airplaneSize,
            @RequestParam(required = false) Long airportId,
            @RequestParam(defaultValue = "registrationNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AirplaneDto> airplanePage = airplaneService.getAllAirplanes(pageable, search, airplaneSize, airportId);

            Pagination pagination = new Pagination(
                    airplanePage.getNumber(),
                    airplanePage.getSize(),
                    airplanePage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(airplanePage.getContent(), pagination));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve airplanes", "Internal server error"));
        }
    }

    @GetMapping("/all")
    @Operation(
        summary = "Get all airplanes (no pagination)",
        description = "Retrieve all airplanes without pagination",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplanes retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirplaneDto>>> getAllAirplanesNoPagination() {
        try {
            List<AirplaneDto> airplanes = airplaneService.getAllAirplanes();
            return ResponseEntity.ok(ApiResponse.success(airplanes, "Retrieved " + airplanes.size() + " airplanes"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airplanes", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get airplane by ID",
        description = "Retrieve a specific airplane by ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplane found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airplane not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<AirplaneDto>> getAirplaneById(
        @Parameter(description = "Airplane ID", required = true)
        @PathVariable Long id) {
        return airplaneService.getAirplaneById(id)
                .map(airplane -> ResponseEntity.ok(ApiResponse.success(airplane, "Airplane found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Airplane not found with ID: " + id)));
    }

    @GetMapping("/registration/{registrationNumber}")
    @Operation(
        summary = "Get airplane by registration number",
        description = "Retrieve a specific airplane by registration number",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplane found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airplane not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<AirplaneDto>> getAirplaneByRegistrationNumber(
        @Parameter(description = "Airplane registration number", required = true)
        @PathVariable String registrationNumber) {
        return airplaneService.getAirplaneByRegistrationNumber(registrationNumber)
                .map(airplane -> ResponseEntity.ok(ApiResponse.success(airplane, "Airplane found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Airplane not found with registration number: " + registrationNumber)));
    }

    @GetMapping("/size/{size}")
    @Operation(
        summary = "Get airplanes by size",
        description = "Retrieve all airplanes of a specific size",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplanes found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirplaneDto>>> getAirplanesBySize(
        @Parameter(description = "Airplane size", required = true)
        @PathVariable AirplaneSize size) {
        try {
            List<AirplaneDto> airplanes = airplaneService.getAirplanesBySize(size);
            return ResponseEntity.ok(ApiResponse.success(airplanes, 
                "Found " + airplanes.size() + " " + size + " airplanes"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airplanes by size", "Internal server error"));
        }
    }

    @GetMapping("/airport/{airportId}")
    @Operation(
        summary = "Get airplanes by current airport",
        description = "Retrieve all airplanes currently located at a specific airport",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplanes found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airport not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirplaneDto>>> getAirplanesByCurrentAirport(
        @Parameter(description = "Airport ID", required = true)
        @PathVariable Long airportId) {
        try {
            List<AirplaneDto> airplanes = airplaneService.getAirplanesByCurrentAirport(airportId);
            return ResponseEntity.ok(ApiResponse.success(airplanes, 
                "Found " + airplanes.size() + " airplanes at airport"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Airport not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to retrieve airplanes"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airplanes by airport", "Internal server error"));
        }
    }

    @GetMapping("/size/{size}/airport/{airportId}")
    @Operation(
        summary = "Get airplanes by size and current airport",
        description = "Retrieve all airplanes of a specific size currently located at a specific airport",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplanes found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airport not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirplaneDto>>> getAirplanesBySizeAndCurrentAirport(
        @Parameter(description = "Airplane size", required = true)
        @PathVariable AirplaneSize size,
        @Parameter(description = "Airport ID", required = true)
        @PathVariable Long airportId) {
        try {
            List<AirplaneDto> airplanes = airplaneService.getAirplanesBySizeAndCurrentAirport(size, airportId);
            return ResponseEntity.ok(ApiResponse.success(airplanes, 
                "Found " + airplanes.size() + " " + size + " airplanes at airport"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Airport not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to retrieve airplanes"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airplanes by size and airport", "Internal server error"));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Create a new airplane",
        description = "Create a new airplane in the system. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplane created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid airplane data",
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
    public ResponseEntity<ApiResponse<AirplaneDto>> createAirplane(
        @Parameter(description = "Airplane data", required = true)
        @Valid @RequestBody Airplane airplane) {
        try {
            AirplaneDto createdAirplane = airplaneService.createAirplane(airplane);
            return ResponseEntity.ok(ApiResponse.success(createdAirplane, "Airplane created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to create airplane"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update airplane",
        description = "Update an existing airplane. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplane updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airplane not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid airplane data",
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
    public ResponseEntity<ApiResponse<AirplaneDto>> updateAirplane(
        @Parameter(description = "Airplane ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated airplane data", required = true)
        @Valid @RequestBody Airplane airplane) {
        try {
            AirplaneDto updatedAirplane = airplaneService.updateAirplane(id, airplane);
            return ResponseEntity.ok(ApiResponse.success(updatedAirplane, "Airplane updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Airplane not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update airplane"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to update airplane", "Internal server error"));
        }
    }

    @PutMapping("/{airplaneId}/location/{airportId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update airplane location",
        description = "Update the current airport location of an airplane. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplane location updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airplane or airport not found",
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
    public ResponseEntity<ApiResponse<AirplaneDto>> updateAirplaneLocation(
        @Parameter(description = "Airplane ID", required = true)
        @PathVariable Long airplaneId,
        @Parameter(description = "Airport ID", required = true)
        @PathVariable Long airportId) {
        try {
            AirplaneDto updatedAirplane = airplaneService.updateCurrentAirport(airplaneId, airportId);
            return ResponseEntity.ok(ApiResponse.success(updatedAirplane, "Airplane location updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Resource not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update airplane location"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to update airplane location", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
        summary = "Delete airplane",
        description = "Delete an airplane from the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airplane deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Airplane not found",
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
    public ResponseEntity<ApiResponse<Void>> deleteAirplane(
        @Parameter(description = "Airplane ID", required = true)
        @PathVariable Long id) {
        try {
            airplaneService.deleteAirplane(id);
            return ResponseEntity.ok(ApiResponse.success("Airplane deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Airplane not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to delete airplane"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to delete airplane", "Internal server error"));
        }
    }
} 