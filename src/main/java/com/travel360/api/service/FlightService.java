package com.travel360.api.service;

import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.model.Airport;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightService {
    
    List<Flight> getAllFlights();
    
    List<FlightDto> getAllFlightsDto();
    
    Optional<Flight> getFlightById(Long id);
    
    Optional<Flight> getFlightByNumber(String flightNumber);
    
    Flight createFlight(Flight flight);
    
    Flight updateFlight(Flight flight);
    
    void deleteFlight(Long id);
    
    List<FlightDto> searchDirectFlights(FlightSearchRequest request);
    
    List<List<FlightDto>> searchConnectingFlights(FlightSearchRequest request);
    
    List<FlightDto> getDepartingFlights(Airport airport, LocalDateTime startTime, LocalDateTime endTime);
    
    List<FlightDto> getArrivingFlights(Airport airport, LocalDateTime startTime, LocalDateTime endTime);
    
    List<Flight> getFlightsByStatus(FlightStatus status);
    
    Flight updateFlightStatus(Long id, FlightStatus status);
} 