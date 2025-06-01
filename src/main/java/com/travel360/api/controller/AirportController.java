package com.travel360.api.controller;

import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.dto.common.Pagination;
import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.model.Airport;
import com.travel360.api.service.AirportService;
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
@RequestMapping("/api/airports")
@Tag(name = "Airport Management", description = "APIs for managing airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    @Operation(
        summary = "Get all airports",
        description = "Retrieve all airports with optional pagination and search functionality",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airports retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAllAirports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AirportDto> airportPage = airportService.getAllAirports(pageable, search);

            Pagination pagination = new Pagination(
                    airportPage.getNumber(),
                    airportPage.getSize(),
                    airportPage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(airportPage.getContent(), pagination));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve airports", "Internal server error"));
        }
    }

    @GetMapping("/all")
    @Operation(
        summary = "Get all airports (no pagination)",
        description = "Retrieve all airports without pagination",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airports retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAllAirportsNoPagination() {
        try {
            List<AirportDto> airports = airportService.getAllAirports();
            return ResponseEntity.ok(ApiResponse.success(airports, "Retrieved " + airports.size() + " airports"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airports", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get airport by ID",
        description = "Retrieve a specific airport by ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airport found",
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
    public ResponseEntity<ApiResponse<AirportDto>> getAirportById(
        @Parameter(description = "Airport ID", required = true)
        @PathVariable Long id) {
        return airportService.getAirportById(id)
                .map(airport -> ResponseEntity.ok(ApiResponse.success(airport, "Airport found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Airport not found with ID: " + id)));
    }

    @GetMapping("/code/{code}")
    @Operation(
        summary = "Get airport by code",
        description = "Retrieve a specific airport by IATA code",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airport found",
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
    public ResponseEntity<ApiResponse<AirportDto>> getAirportByCode(
        @Parameter(description = "Airport IATA code", required = true)
        @PathVariable String code) {
        return airportService.getAirportByCode(code)
                .map(airport -> ResponseEntity.ok(ApiResponse.success(airport, "Airport found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Airport not found with code: " + code)));
    }

    @GetMapping("/country/{country}")
    @Operation(
        summary = "Get airports by country",
        description = "Retrieve all airports in a specific country",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airports found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAirportsByCountry(
        @Parameter(description = "Country name", required = true)
        @PathVariable String country) {
        try {
            List<AirportDto> airports = airportService.getAirportsByCountry(country);
            return ResponseEntity.ok(ApiResponse.success(airports, 
                "Found " + airports.size() + " airports in " + country));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airports by country", "Internal server error"));
        }
    }

    @GetMapping("/city/{city}")
    @Operation(
        summary = "Get airports by city",
        description = "Retrieve all airports in a specific city",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airports found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AirportDto>>> getAirportsByCity(
        @Parameter(description = "City name", required = true)
        @PathVariable String city) {
        try {
            List<AirportDto> airports = airportService.getAirportsByCity(city);
            return ResponseEntity.ok(ApiResponse.success(airports, 
                "Found " + airports.size() + " airports in " + city));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve airports by city", "Internal server error"));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Create a new airport",
        description = "Create a new airport in the system. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airport created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid airport data",
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
    public ResponseEntity<ApiResponse<AirportDto>> createAirport(
        @Parameter(description = "Airport data", required = true)
        @Valid @RequestBody Airport airport) {
        try {
            AirportDto createdAirport = airportService.createAirport(airport);
            return ResponseEntity.ok(ApiResponse.success(createdAirport, "Airport created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to create airport"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update airport",
        description = "Update an existing airport. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airport updated successfully",
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
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid airport data",
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
    public ResponseEntity<ApiResponse<AirportDto>> updateAirport(
        @Parameter(description = "Airport ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated airport data", required = true)
        @Valid @RequestBody Airport airport) {
        try {
            AirportDto updatedAirport = airportService.updateAirport(id, airport);
            return ResponseEntity.ok(ApiResponse.success(updatedAirport, "Airport updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Airport not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update airport"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to update airport", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
        summary = "Delete airport",
        description = "Delete an airport from the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Airport deleted successfully",
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
    public ResponseEntity<ApiResponse<Void>> deleteAirport(
        @Parameter(description = "Airport ID", required = true)
        @PathVariable Long id) {
        try {
            airportService.deleteAirport(id);
            return ResponseEntity.ok(ApiResponse.success("Airport deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Airport not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to delete airport"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to delete airport", "Internal server error"));
        }
    }
} 