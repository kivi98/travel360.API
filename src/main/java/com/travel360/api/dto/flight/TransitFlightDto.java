package com.travel360.api.dto.flight;

import com.travel360.api.model.SeatClass;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransitFlightDto {
    
    private List<DirectFlightDto> segments;
    private AirportDto originAirport;
    private AirportDto destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int totalDurationMinutes;
    private int totalDistanceKm;
    private int numberOfStops;
    private List<AirportDto> transitAirports;
    
    // Pricing information for the requested seat class
    private BigDecimal totalPrice;
    private SeatClass seatClass;
    private int minAvailableSeats; // Minimum available seats across all segments
    
    // Connection information
    private List<Integer> connectionTimeMinutes; // Time between each segment
    private int totalConnectionTimeMinutes;
    
    // Summary information
    private String routeSummary; // e.g., "JFK → LHR → CDG"
    private String airlinesSummary; // Airlines involved in the journey
} 