package com.travel360.api.dto.flight;

import com.travel360.api.model.FlightStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlightDto {
    
    private Long id;
    private String flightNumber;
    private AirportDto originAirport;
    private AirportDto destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal firstClassPrice;
    private BigDecimal businessClassPrice;
    private BigDecimal economyClassPrice;
    private int firstClassAvailableSeats;
    private int businessClassAvailableSeats;
    private int economyClassAvailableSeats;
    private FlightStatus status;
    private String airplaneModel;
    private String airplaneRegistration;
    private int distanceKm;
    private int durationMinutes;
} 