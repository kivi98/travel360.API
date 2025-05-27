package com.travel360.api.controller;

import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "Flight Management", description = "APIs for managing flights including search, CRUD operations, and status updates")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    @Operation(
        summary = "Get all flights",
        description = "Retrieves a list of all available flights in the system",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved flights",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
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
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAllFlights() {
        try {
            List<FlightDto> flights = flightService.getAllFlightsDto();
            return ResponseEntity.ok(ApiResponse.success(flights, "Flights retrieved successfully"));
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
    public ResponseEntity<ApiResponse<Flight>> getFlightById(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id) {
        Optional<Flight> flight = flightService.getFlightById(id);
        if (flight.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(flight.get(), "Flight found"));
        } else {
            return ResponseEntity.notFound()
                .header("Content-Type", "application/json")
                .build();
        }
    }

    @GetMapping("/number/{flightNumber}")
    @Operation(
        summary = "Get flight by flight number",
        description = "Retrieve a specific flight by its flight number",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Flight>> getFlightByNumber(
        @Parameter(description = "Flight number", required = true)
        @PathVariable String flightNumber) {
        Optional<Flight> flight = flightService.getFlightByNumber(flightNumber);
        if (flight.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(flight.get(), "Flight found"));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("Flight not found with number: " + flightNumber));
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
            description = "Invalid flight data provided",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions (requires OPERATOR or ADMINISTRATOR role)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Flight>> createFlight(
        @Parameter(description = "Flight data to create", required = true)
        @Valid @RequestBody Flight flight) {
        try {
            Flight createdFlight = flightService.createFlight(flight);
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
    public ResponseEntity<ApiResponse<Flight>> updateFlight(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id, 
        @Parameter(description = "Updated flight data", required = true)
        @Valid @RequestBody Flight flight) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("Flight not found with ID: " + id));
        }
        try {
            flight.setId(id);
            Flight updatedFlight = flightService.updateFlight(flight);
            return ResponseEntity.ok(ApiResponse.success(updatedFlight, "Flight updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update flight"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
        summary = "Delete flight",
        description = "Delete a flight from the system. Requires ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> deleteFlight(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("Flight not found with ID: " + id));
        }
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.ok(ApiResponse.success("Flight deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(e.getMessage(), "Failed to delete flight"));
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
            description = "Invalid search criteria provided",
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
                "No flights found matching your criteria" : 
                "Found " + flights.size() + " matching flights";
            return ResponseEntity.ok(ApiResponse.success(flights, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Flight search failed"));
        }
    }

    @PostMapping("/search/connecting")
    @Operation(
        summary = "Search for connecting flights",
        description = "Search for available connecting flights (multi-leg journeys). This endpoint is publicly accessible."
    )
    public ResponseEntity<ApiResponse<List<List<FlightDto>>>> searchConnectingFlights(
        @Parameter(description = "Flight search criteria for connecting flights", required = true)
        @Valid @RequestBody FlightSearchRequest searchRequest) {
        try {
            List<List<FlightDto>> connectingFlights = flightService.searchConnectingFlights(searchRequest);
            String message = connectingFlights.isEmpty() ? 
                "No connecting flights found matching your criteria" : 
                "Found " + connectingFlights.size() + " connecting flight options";
            return ResponseEntity.ok(ApiResponse.success(connectingFlights, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Connecting flights search failed"));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update flight status",
        description = "Update the status of a flight (e.g., ON_TIME, DELAYED, CANCELLED). Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Flight>> updateFlightStatus(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id, 
        @Parameter(description = "New flight status", required = true)
        @RequestBody FlightStatus status) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("Flight not found with ID: " + id));
        }
        try {
            Flight updatedFlight = flightService.updateFlightStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(updatedFlight, "Flight status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update flight status"));
        }
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get flights by status",
        description = "Retrieve all flights with a specific status",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<Flight>>> getFlightsByStatus(
        @Parameter(description = "Flight status to filter by", required = true)
        @PathVariable FlightStatus status) {
        try {
            List<Flight> flights = flightService.getFlightsByStatus(status);
            String message = flights.isEmpty() ? 
                "No flights found with status: " + status : 
                "Found " + flights.size() + " flights with status: " + status;
            return ResponseEntity.ok(ApiResponse.success(flights, message));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve flights by status", "Internal server error"));
        }
    }
} 