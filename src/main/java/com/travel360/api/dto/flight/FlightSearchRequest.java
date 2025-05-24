package com.travel360.api.dto.flight;

import com.travel360.api.model.SeatClass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FlightSearchRequest {
    
    @NotNull
    private Long originAirportId;
    
    @NotNull
    private Long destinationAirportId;
    
    @NotNull
    private LocalDate departureDate;
    
    @NotNull
    private SeatClass seatClass;
    
    private boolean includeTransits = true;
    
    private int passengerCount = 1;
} 