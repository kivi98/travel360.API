package com.travel360.api.controller;

import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import com.travel360.api.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
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
    public ResponseEntity<Flight> createFlight(@Valid @RequestBody Flight flight) {
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
    public ResponseEntity<List<FlightDto>> searchDirectFlights(@Valid @RequestBody FlightSearchRequest searchRequest) {
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