package com.travel360.api.service.impl;

import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.model.Airport;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import com.travel360.api.repository.AirplaneRepository;
import com.travel360.api.repository.AirportRepository;
import com.travel360.api.repository.FlightRepository;
import com.travel360.api.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private AirportRepository airportRepository;
    
    @Autowired
    private AirplaneRepository airplaneRepository;

    @Override
    public List<FlightDto> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<FlightDto> getAllFlights(Pageable pageable, String search, FlightStatus status, Long originAirportId, Long destinationAirportId) {
        Page<Flight> flightPage = flightRepository.findBySearchCriteria(pageable, search, status, originAirportId, destinationAirportId);
        return flightPage.map(this::convertToDto);
    }

    @Override
    public Optional<FlightDto> getFlightById(Long id) {
        return flightRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<FlightDto> getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .map(this::convertToDto);
    }

    @Override
    public FlightDto createFlight(Flight flight) {
        if (existsByFlightNumber(flight.getFlightNumber())) {
            throw new RuntimeException("Flight with number " + flight.getFlightNumber() + " already exists");
        }
        
        // Validate and set airplane
        if (flight.getAirplane() != null && flight.getAirplane().getId() != null) {
            flight.setAirplane(airplaneRepository.findById(flight.getAirplane().getId())
                    .orElseThrow(() -> new RuntimeException("Airplane not found with ID: " + flight.getAirplane().getId())));
        }
        
        // Validate and set origin airport
        if (flight.getOriginAirport() != null && flight.getOriginAirport().getId() != null) {
            flight.setOriginAirport(airportRepository.findById(flight.getOriginAirport().getId())
                    .orElseThrow(() -> new RuntimeException("Origin airport not found with ID: " + flight.getOriginAirport().getId())));
        }
        
        // Validate and set destination airport
        if (flight.getDestinationAirport() != null && flight.getDestinationAirport().getId() != null) {
            flight.setDestinationAirport(airportRepository.findById(flight.getDestinationAirport().getId())
                    .orElseThrow(() -> new RuntimeException("Destination airport not found with ID: " + flight.getDestinationAirport().getId())));
        }
        
        // Initialize available seats based on airplane capacity
        if (flight.getAirplane() != null) {
            flight.setFirstClassAvailableSeats(flight.getAirplane().getFirstClassCapacity());
            flight.setBusinessClassAvailableSeats(flight.getAirplane().getBusinessClassCapacity());
            flight.setEconomyClassAvailableSeats(flight.getAirplane().getEconomyClassCapacity());
        }
        
        // Set default status if not provided
        if (flight.getStatus() == null) {
            flight.setStatus(FlightStatus.SCHEDULED);
        }
        
        Flight savedFlight = flightRepository.save(flight);
        return convertToDto(savedFlight);
    }

    @Override
    public FlightDto updateFlight(Long id, Flight flight) {
        return flightRepository.findById(id)
                .map(existingFlight -> {
                    // Check if flight number is being changed and if new number already exists
                    if (!existingFlight.getFlightNumber().equals(flight.getFlightNumber()) 
                        && existsByFlightNumber(flight.getFlightNumber())) {
                        throw new RuntimeException("Flight with number " + flight.getFlightNumber() + " already exists");
                    }
                    
                    existingFlight.setFlightNumber(flight.getFlightNumber());
                    existingFlight.setDepartureTime(flight.getDepartureTime());
                    existingFlight.setArrivalTime(flight.getArrivalTime());
                    existingFlight.setFirstClassPrice(flight.getFirstClassPrice());
                    existingFlight.setBusinessClassPrice(flight.getBusinessClassPrice());
                    existingFlight.setEconomyClassPrice(flight.getEconomyClassPrice());
                    existingFlight.setDistanceKm(flight.getDistanceKm());
                    
                    // Update airplane if provided
                    if (flight.getAirplane() != null && flight.getAirplane().getId() != null) {
                        existingFlight.setAirplane(airplaneRepository.findById(flight.getAirplane().getId())
                                .orElseThrow(() -> new RuntimeException("Airplane not found with ID: " + flight.getAirplane().getId())));
                    }
                    
                    // Update origin airport if provided
                    if (flight.getOriginAirport() != null && flight.getOriginAirport().getId() != null) {
                        existingFlight.setOriginAirport(airportRepository.findById(flight.getOriginAirport().getId())
                                .orElseThrow(() -> new RuntimeException("Origin airport not found with ID: " + flight.getOriginAirport().getId())));
                    }
                    
                    // Update destination airport if provided
                    if (flight.getDestinationAirport() != null && flight.getDestinationAirport().getId() != null) {
                        existingFlight.setDestinationAirport(airportRepository.findById(flight.getDestinationAirport().getId())
                                .orElseThrow(() -> new RuntimeException("Destination airport not found with ID: " + flight.getDestinationAirport().getId())));
                    }
                    
                    Flight updatedFlight = flightRepository.save(existingFlight);
                    return convertToDto(updatedFlight);
                })
                .orElseThrow(() -> new RuntimeException("Flight not found with ID: " + id));
    }

    @Override
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Flight not found with ID: " + id);
        }
        flightRepository.deleteById(id);
    }

    @Override
    public List<FlightDto> searchDirectFlights(FlightSearchRequest request) {
        LocalDateTime departureDateTime = request.getDepartureDate().atStartOfDay();
        List<Flight> flights = flightRepository.findAvailableDirectFlights(
                request.getOriginAirportId(),
                request.getDestinationAirportId(),
                departureDateTime
        );
        return flights.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<List<FlightDto>> searchConnectingFlights(FlightSearchRequest request) {
        List<List<FlightDto>> connectingFlights = new ArrayList<>();
        
        LocalDateTime departureDateTime = request.getDepartureDate().atStartOfDay();
        
        // Find all flights from origin
        List<Flight> firstLegFlights = flightRepository.findAvailableDirectFlights(
                request.getOriginAirportId(),
                null, // Any destination for first leg
                departureDateTime
        );
        
        // For each first leg flight, find connecting flights
        for (Flight firstFlight : firstLegFlights) {
            if (!firstFlight.getDestinationAirport().getId().equals(request.getDestinationAirportId())) {
                // Look for second leg flights from this intermediate airport
                LocalDateTime minConnectionTime = firstFlight.getArrivalTime().plusMinutes(60); // 1 hour minimum connection
                List<Flight> secondLegFlights = flightRepository.findAvailableDirectFlights(
                        firstFlight.getDestinationAirport().getId(),
                        request.getDestinationAirportId(),
                        minConnectionTime
                );
                
                for (Flight secondFlight : secondLegFlights) {
                    if (firstFlight.canConnectTo(secondFlight, 60)) {
                        List<FlightDto> connection = List.of(
                                convertToDto(firstFlight),
                                convertToDto(secondFlight)
                        );
                        connectingFlights.add(connection);
                    }
                }
            }
        }
        
        return connectingFlights;
    }

    @Override
    public List<FlightDto> getDepartingFlights(Airport airport, LocalDateTime startTime, LocalDateTime endTime) {
        List<Flight> flights = flightRepository.findDepartingFlights(airport, startTime, endTime);
        return flights.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<FlightDto> getArrivingFlights(Airport airport, LocalDateTime startTime, LocalDateTime endTime) {
        List<Flight> flights = flightRepository.findArrivingFlights(airport, startTime, endTime);
        return flights.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<FlightDto> getFlightsByStatus(FlightStatus status) {
        List<Flight> flights = flightRepository.findByStatus(status);
        return flights.stream().map(this::convertToDto).toList();
    }

    @Override
    public FlightDto updateFlightStatus(Long id, FlightStatus status) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with ID: " + id));
        
        flight.setStatus(status);
        Flight updatedFlight = flightRepository.save(flight);
        return convertToDto(updatedFlight);
    }

    @Override
    public boolean existsByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber).isPresent();
    }

    private FlightDto convertToDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setFirstClassPrice(flight.getFirstClassPrice());
        dto.setBusinessClassPrice(flight.getBusinessClassPrice());
        dto.setEconomyClassPrice(flight.getEconomyClassPrice());
        dto.setFirstClassAvailableSeats(flight.getFirstClassAvailableSeats());
        dto.setBusinessClassAvailableSeats(flight.getBusinessClassAvailableSeats());
        dto.setEconomyClassAvailableSeats(flight.getEconomyClassAvailableSeats());
        dto.setStatus(flight.getStatus());
        dto.setDistanceKm(flight.getDistanceKm());
        
        // Calculate duration in minutes
        if (flight.getDepartureTime() != null && flight.getArrivalTime() != null) {
            dto.setDurationMinutes((int) ChronoUnit.MINUTES.between(flight.getDepartureTime(), flight.getArrivalTime()));
        }
        
        // Convert airplane info
        if (flight.getAirplane() != null) {
            dto.setAirplaneModel(flight.getAirplane().getModel());
            dto.setAirplaneRegistration(flight.getAirplane().getRegistrationNumber());
        }
        
        // Convert origin airport
        if (flight.getOriginAirport() != null) {
            AirportDto originDto = new AirportDto();
            originDto.setId(flight.getOriginAirport().getId());
            originDto.setCode(flight.getOriginAirport().getCode());
            originDto.setName(flight.getOriginAirport().getName());
            originDto.setCity(flight.getOriginAirport().getCity());
            originDto.setCountry(flight.getOriginAirport().getCountry());
            originDto.setLatitude(flight.getOriginAirport().getLatitude());
            originDto.setLongitude(flight.getOriginAirport().getLongitude());
            originDto.setTimeZone(flight.getOriginAirport().getTimeZone());
            dto.setOriginAirport(originDto);
        }
        
        // Convert destination airport
        if (flight.getDestinationAirport() != null) {
            AirportDto destinationDto = new AirportDto();
            destinationDto.setId(flight.getDestinationAirport().getId());
            destinationDto.setCode(flight.getDestinationAirport().getCode());
            destinationDto.setName(flight.getDestinationAirport().getName());
            destinationDto.setCity(flight.getDestinationAirport().getCity());
            destinationDto.setCountry(flight.getDestinationAirport().getCountry());
            destinationDto.setLatitude(flight.getDestinationAirport().getLatitude());
            destinationDto.setLongitude(flight.getDestinationAirport().getLongitude());
            destinationDto.setTimeZone(flight.getDestinationAirport().getTimeZone());
            dto.setDestinationAirport(destinationDto);
        }
        
        return dto;
    }
} 