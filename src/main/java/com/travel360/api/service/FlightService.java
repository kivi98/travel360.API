package com.travel360.api.service;

import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.model.Airport;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightService {
    
    List<FlightDto> getAllFlights();
    
    Page<FlightDto> getAllFlights(Pageable pageable, String search, FlightStatus status, Long originAirportId, Long destinationAirportId);
    
    Optional<FlightDto> getFlightById(Long id);
    
    Optional<FlightDto> getFlightByNumber(String flightNumber);
    
    FlightDto createFlight(Flight flight);
    
    FlightDto updateFlight(Long id, Flight flight);
    
    void deleteFlight(Long id);
    
    List<FlightDto> searchDirectFlights(FlightSearchRequest request);
    
    List<List<FlightDto>> searchConnectingFlights(FlightSearchRequest request);
    
    List<FlightDto> getDepartingFlights(Airport airport, LocalDateTime startTime, LocalDateTime endTime);
    
    List<FlightDto> getArrivingFlights(Airport airport, LocalDateTime startTime, LocalDateTime endTime);
    
    List<FlightDto> getFlightsByStatus(FlightStatus status);
    
    FlightDto updateFlightStatus(Long id, FlightStatus status);
    
    boolean existsByFlightNumber(String flightNumber);
} 