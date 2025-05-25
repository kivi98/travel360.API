package com.travel360.api.controller;

import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import com.travel360.api.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
        description = "Retrieves a list of all available flights in the system",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved flights",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = FlightDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content
        )
    })
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlightsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        return flightService.getFlightByNumber(flightNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Create a new flight",
        description = "Create a new flight in the system. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Flight created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Flight.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid flight data provided",
            content = @Content
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
    public ResponseEntity<Flight> createFlight(
        @Parameter(description = "Flight data to create", required = true)
        @Valid @RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.createFlight(flight));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @Valid @RequestBody Flight flight) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        flight.setId(id);
        return ResponseEntity.ok(flightService.updateFlight(flight));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> deleteFlight(@PathVariable Long id) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        flightService.deleteFlight(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    @Operation(
        summary = "Search for direct flights",
        description = "Search for available direct flights based on departure/arrival airports and dates. This endpoint is publicly accessible."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully found matching flights",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = FlightDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search criteria provided",
            content = @Content
        )
    })
    public ResponseEntity<List<FlightDto>> searchDirectFlights(
        @Parameter(description = "Flight search criteria including airports and dates", required = true)
        @Valid @RequestBody FlightSearchRequest searchRequest) {
        return ResponseEntity.ok(flightService.searchDirectFlights(searchRequest));
    }

    @PostMapping("/search/connecting")
    public ResponseEntity<List<List<FlightDto>>> searchConnectingFlights(@Valid @RequestBody FlightSearchRequest searchRequest) {
        return ResponseEntity.ok(flightService.searchConnectingFlights(searchRequest));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<Flight> updateFlightStatus(@PathVariable Long id, @RequestBody FlightStatus status) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flightService.updateFlightStatus(id, status));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Flight>> getFlightsByStatus(@PathVariable FlightStatus status) {
        return ResponseEntity.ok(flightService.getFlightsByStatus(status));
    }
} 