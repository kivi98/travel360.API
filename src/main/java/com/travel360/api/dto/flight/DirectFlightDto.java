package com.travel360.api.dto.flight;

import com.travel360.api.model.FlightStatus;
import com.travel360.api.model.SeatClass;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DirectFlightDto {
    
    private Long id;
    private String flightNumber;
    private AirportDto originAirport;
    private AirportDto destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int durationMinutes;
    private int distanceKm;
    private FlightStatus status;
    private String airplaneModel;
    private String airplaneRegistration;
    
    // Price and availability for the requested seat class
    private BigDecimal price;
    private int availableSeats;
    private SeatClass seatClass;
    
    // All seat class information (for flexibility)
    private BigDecimal firstClassPrice;
    private BigDecimal businessClassPrice;
    private BigDecimal economyClassPrice;
    private int firstClassAvailableSeats;
    private int businessClassAvailableSeats;
    private int economyClassAvailableSeats;
} 