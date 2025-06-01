package com.travel360.api.controller;

import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.dto.common.Pagination;
import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.dto.flight.FlightSearchResponse;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import com.travel360.api.service.FlightService;
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
@RequestMapping("/api/flights")
@Tag(name = "Flight Management", description = "APIs for managing flights including search, CRUD operations, and status updates")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    @Operation(
        summary = "Get all flights",
        description = "Retrieve all flights with optional pagination, search, and filtering functionality",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flights retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAllFlights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) FlightStatus status,
            @RequestParam(required = false) Long originAirportId,
            @RequestParam(required = false) Long destinationAirportId,
            @RequestParam(defaultValue = "flightNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<FlightDto> flightPage = flightService.getAllFlights(pageable, search, status, originAirportId, destinationAirportId);

            Pagination pagination = new Pagination(
                    flightPage.getNumber(),
                    flightPage.getSize(),
                    flightPage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(flightPage.getContent(), pagination));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve flights", "Internal server error"));
        }
    }

    @GetMapping("/all")
    @Operation(
        summary = "Get all flights (no pagination)",
        description = "Retrieve all flights without pagination",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flights retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAllFlightsNoPagination() {
        try {
            List<FlightDto> flights = flightService.getAllFlights();
            return ResponseEntity.ok(ApiResponse.success(flights, "Retrieved " + flights.size() + " flights"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve flights", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get flight by ID",
        description = "Retrieve a specific flight by its ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Flight not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<FlightDto>> getFlightById(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(flight -> ResponseEntity.ok(ApiResponse.success(flight, "Flight found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Flight not found with ID: " + id)));
    }

    @GetMapping("/number/{flightNumber}")
    @Operation(
        summary = "Get flight by flight number",
        description = "Retrieve a specific flight by its flight number",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Flight not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<FlightDto>> getFlightByNumber(
        @Parameter(description = "Flight number", required = true)
        @PathVariable String flightNumber) {
        return flightService.getFlightByNumber(flightNumber)
                .map(flight -> ResponseEntity.ok(ApiResponse.success(flight, "Flight found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Flight not found with number: " + flightNumber)));
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get flights by status",
        description = "Retrieve all flights with a specific status",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flights found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<FlightDto>>> getFlightsByStatus(
        @Parameter(description = "Flight status to filter by", required = true)
        @PathVariable FlightStatus status) {
        try {
            List<FlightDto> flights = flightService.getFlightsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(flights, 
                "Found " + flights.size() + " flights with status " + status));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve flights by status", "Internal server error"));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Create a new flight",
        description = "Create a new flight in the system. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid flight data",
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
    public ResponseEntity<ApiResponse<FlightDto>> createFlight(
        @Parameter(description = "Flight data", required = true)
        @Valid @RequestBody Flight flight) {
        try {
            FlightDto createdFlight = flightService.createFlight(flight);
            return ResponseEntity.ok(ApiResponse.success(createdFlight, "Flight created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to create flight"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update flight",
        description = "Update an existing flight. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Flight not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid flight data",
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
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated flight data", required = true)
        @Valid @RequestBody Flight flight) {
        try {
            FlightDto updatedFlight = flightService.updateFlight(id, flight);
            return ResponseEntity.ok(ApiResponse.success(updatedFlight, "Flight updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Flight not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update flight"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to update flight", "Internal server error"));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update flight status",
        description = "Update the status of a flight (e.g., ON_TIME, DELAYED, CANCELLED). Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight status updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Flight not found",
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
    public ResponseEntity<ApiResponse<FlightDto>> updateFlightStatus(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id, 
        @Parameter(description = "New flight status", required = true)
        @RequestBody FlightStatus status) {
        try {
            FlightDto updatedFlight = flightService.updateFlightStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(updatedFlight, "Flight status updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Flight not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update flight status"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to update flight status", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
        summary = "Delete flight",
        description = "Delete a flight from the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Flight not found",
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
    public ResponseEntity<ApiResponse<Void>> deleteFlight(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.ok(ApiResponse.success("Flight deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage(), "Flight not found"));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to delete flight"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to delete flight", "Internal server error"));
        }
    }

    @PostMapping("/search")
    @Operation(
        summary = "Search for direct flights",
        description = "Search for available direct flights based on departure/arrival airports and dates. This endpoint is publicly accessible."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully found matching flights",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid search criteria",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<FlightDto>>> searchDirectFlights(
        @Parameter(description = "Flight search criteria including airports and dates", required = true)
        @Valid @RequestBody FlightSearchRequest searchRequest) {
        try {
            List<FlightDto> flights = flightService.searchDirectFlights(searchRequest);
            String message = flights.isEmpty() ? 
                "No direct flights found for the specified criteria" : 
                "Found " + flights.size() + " direct flights";
            return ResponseEntity.ok(ApiResponse.success(flights, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to search flights"));
        }
    }

    @PostMapping("/search/connecting")
    @Operation(
        summary = "Search for connecting flights",
        description = "Search for available connecting flights (multi-leg journeys). This endpoint is publicly accessible."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully found connecting flight options",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid search criteria",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<List<FlightDto>>>> searchConnectingFlights(
        @Parameter(description = "Flight search criteria for connecting flights", required = true)
        @Valid @RequestBody FlightSearchRequest searchRequest) {
        try {
            List<List<FlightDto>> connectingFlights = flightService.searchConnectingFlights(searchRequest);
            String message = connectingFlights.isEmpty() ? 
                "No connecting flights found for the specified criteria" : 
                "Found " + connectingFlights.size() + " connecting flight options";
            return ResponseEntity.ok(ApiResponse.success(connectingFlights, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to search connecting flights"));
        }
    }

    @PostMapping("/search/comprehensive")
    @Operation(
        summary = "Comprehensive flight search",
        description = "Search for both direct and transit flights in a single request. Returns structured data with separate lists for direct flights and transit flight options. This endpoint is publicly accessible."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully found matching flights",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid search criteria",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<FlightSearchResponse>> searchFlightsComprehensive(
        @Parameter(description = "Flight search criteria including airports, dates, seat class, and passenger count", required = true)
        @Valid @RequestBody FlightSearchRequest searchRequest) {
        try {
            FlightSearchResponse searchResponse = flightService.searchFlights(searchRequest);
            return ResponseEntity.ok(ApiResponse.success(searchResponse, searchResponse.getSearchSummary()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to search flights"));
        }
    }
} 