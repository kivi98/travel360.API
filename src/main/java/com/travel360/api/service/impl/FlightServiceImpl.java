package com.travel360.api.service.impl;

import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.dto.flight.DirectFlightDto;
import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.dto.flight.FlightSearchRequest;
import com.travel360.api.dto.flight.FlightSearchResponse;
import com.travel360.api.dto.flight.TransitFlightDto;
import com.travel360.api.model.Airport;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import com.travel360.api.model.SeatClass;
import com.travel360.api.repository.AirplaneRepository;
import com.travel360.api.repository.AirportRepository;
import com.travel360.api.repository.FlightRepository;
import com.travel360.api.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public FlightSearchResponse searchFlights(FlightSearchRequest request) {
        // Search for direct flights
        List<DirectFlightDto> directFlights = searchDirectFlightsEnhanced(request);
        
        // Search for transit flights if requested
        List<TransitFlightDto> transitFlights = new ArrayList<>();
        if (request.isIncludeTransits()) {
            transitFlights = searchTransitFlights(request);
        }
        
        return new FlightSearchResponse(directFlights, transitFlights);
    }
    
    private List<DirectFlightDto> searchDirectFlightsEnhanced(FlightSearchRequest request) {
        LocalDateTime departureDateTime = request.getDepartureDate().atStartOfDay();
        LocalDateTime endOfDay = departureDateTime.plusDays(1).minusMinutes(1);
        
        List<Flight> flights = flightRepository.findAvailableDirectFlights(
                request.getOriginAirportId(),
                request.getDestinationAirportId(),
                departureDateTime
        );
        
        return flights.stream()
                .filter(flight -> flight.getDepartureTime().isBefore(endOfDay))
                .filter(flight -> hasAvailableSeats(flight, request.getSeatClass(), request.getPassengerCount()))
                .map(flight -> convertToDirectFlightDto(flight, request.getSeatClass()))
                .collect(Collectors.toList());
    }
    
    private List<TransitFlightDto> searchTransitFlights(FlightSearchRequest request) {
        List<TransitFlightDto> transitFlights = new ArrayList<>();
        LocalDateTime departureDateTime = request.getDepartureDate().atStartOfDay();
        LocalDateTime endOfDay = departureDateTime.plusDays(1).minusMinutes(1);
        
        // Find all flights from origin on the departure date
        List<Flight> firstLegFlights = flightRepository.findAvailableDirectFlights(
                request.getOriginAirportId(),
                null, // Any destination for first leg
                departureDateTime
        ).stream()
                .filter(flight -> flight.getDepartureTime().isBefore(endOfDay))
                .filter(flight -> !flight.getDestinationAirport().getId().equals(request.getDestinationAirportId()))
                .filter(flight -> hasAvailableSeats(flight, request.getSeatClass(), request.getPassengerCount()))
                .collect(Collectors.toList());
        
        // For each first leg flight, find connecting flights
        for (Flight firstFlight : firstLegFlights) {
            // Look for second leg flights from this intermediate airport
            LocalDateTime minConnectionTime = firstFlight.getArrivalTime().plusMinutes(60); // 1 hour minimum connection
            LocalDateTime maxConnectionTime = firstFlight.getArrivalTime().plusHours(6); // 6 hours maximum connection
            
            List<Flight> secondLegFlights = flightRepository.findAvailableDirectFlights(
                    firstFlight.getDestinationAirport().getId(),
                    request.getDestinationAirportId(),
                    minConnectionTime
            ).stream()
                    .filter(flight -> flight.getDepartureTime().isBefore(maxConnectionTime))
                    .filter(flight -> hasAvailableSeats(flight, request.getSeatClass(), request.getPassengerCount()))
                    .collect(Collectors.toList());
            
            for (Flight secondFlight : secondLegFlights) {
                if (firstFlight.canConnectTo(secondFlight, 60)) {
                    TransitFlightDto transitFlight = createTransitFlightDto(
                            List.of(firstFlight, secondFlight), 
                            request.getSeatClass()
                    );
                    transitFlights.add(transitFlight);
                }
            }
        }
        
        return transitFlights;
    }
    
    private boolean hasAvailableSeats(Flight flight, SeatClass seatClass, int passengerCount) {
        return switch (seatClass) {
            case FIRST_CLASS -> flight.getFirstClassAvailableSeats() >= passengerCount;
            case BUSINESS_CLASS -> flight.getBusinessClassAvailableSeats() >= passengerCount;
            case ECONOMY_CLASS -> flight.getEconomyClassAvailableSeats() >= passengerCount;
        };
    }
    
    private DirectFlightDto convertToDirectFlightDto(Flight flight, SeatClass seatClass) {
        DirectFlightDto dto = new DirectFlightDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setStatus(flight.getStatus());
        dto.setDistanceKm(flight.getDistanceKm());
        dto.setSeatClass(seatClass);
        
        // Calculate duration in minutes
        if (flight.getDepartureTime() != null && flight.getArrivalTime() != null) {
            dto.setDurationMinutes((int) ChronoUnit.MINUTES.between(flight.getDepartureTime(), flight.getArrivalTime()));
        }
        
        // Set airplane info
        if (flight.getAirplane() != null) {
            dto.setAirplaneModel(flight.getAirplane().getModel());
            dto.setAirplaneRegistration(flight.getAirplane().getRegistrationNumber());
        }
        
        // Set pricing and availability for requested seat class
        dto.setPrice(flight.getPriceForClass(seatClass));
        dto.setAvailableSeats(getAvailableSeatsForClass(flight, seatClass));
        
        // Set all pricing information
        dto.setFirstClassPrice(flight.getFirstClassPrice());
        dto.setBusinessClassPrice(flight.getBusinessClassPrice());
        dto.setEconomyClassPrice(flight.getEconomyClassPrice());
        dto.setFirstClassAvailableSeats(flight.getFirstClassAvailableSeats());
        dto.setBusinessClassAvailableSeats(flight.getBusinessClassAvailableSeats());
        dto.setEconomyClassAvailableSeats(flight.getEconomyClassAvailableSeats());
        
        // Convert airports
        dto.setOriginAirport(convertToAirportDto(flight.getOriginAirport()));
        dto.setDestinationAirport(convertToAirportDto(flight.getDestinationAirport()));
        
        return dto;
    }
    
    private TransitFlightDto createTransitFlightDto(List<Flight> flights, SeatClass seatClass) {
        TransitFlightDto dto = new TransitFlightDto();
        
        // Convert flight segments
        List<DirectFlightDto> segments = flights.stream()
                .map(flight -> convertToDirectFlightDto(flight, seatClass))
                .collect(Collectors.toList());
        dto.setSegments(segments);
        
        // Set origin and destination
        dto.setOriginAirport(convertToAirportDto(flights.get(0).getOriginAirport()));
        dto.setDestinationAirport(convertToAirportDto(flights.get(flights.size() - 1).getDestinationAirport()));
        
        // Set departure and arrival times
        dto.setDepartureTime(flights.get(0).getDepartureTime());
        dto.setArrivalTime(flights.get(flights.size() - 1).getArrivalTime());
        
        // Calculate total duration and distance
        dto.setTotalDurationMinutes((int) ChronoUnit.MINUTES.between(dto.getDepartureTime(), dto.getArrivalTime()));
        dto.setTotalDistanceKm(flights.stream().mapToInt(Flight::getDistanceKm).sum());
        
        // Set number of stops and transit airports
        dto.setNumberOfStops(flights.size() - 1);
        List<AirportDto> transitAirports = new ArrayList<>();
        for (int i = 0; i < flights.size() - 1; i++) {
            transitAirports.add(convertToAirportDto(flights.get(i).getDestinationAirport()));
        }
        dto.setTransitAirports(transitAirports);
        
        // Calculate pricing
        BigDecimal totalPrice = flights.stream()
                .map(flight -> flight.getPriceForClass(seatClass))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalPrice(totalPrice);
        dto.setSeatClass(seatClass);
        
        // Calculate minimum available seats across all segments
        int minSeats = flights.stream()
                .mapToInt(flight -> getAvailableSeatsForClass(flight, seatClass))
                .min()
                .orElse(0);
        dto.setMinAvailableSeats(minSeats);
        
        // Calculate connection times
        List<Integer> connectionTimes = new ArrayList<>();
        int totalConnectionTime = 0;
        for (int i = 0; i < flights.size() - 1; i++) {
            int connectionTime = (int) ChronoUnit.MINUTES.between(
                    flights.get(i).getArrivalTime(),
                    flights.get(i + 1).getDepartureTime()
            );
            connectionTimes.add(connectionTime);
            totalConnectionTime += connectionTime;
        }
        dto.setConnectionTimeMinutes(connectionTimes);
        dto.setTotalConnectionTimeMinutes(totalConnectionTime);
        
        // Create route summary
        String routeSummary = flights.stream()
                .map(flight -> flight.getOriginAirport().getCode())
                .collect(Collectors.joining(" → "));
        routeSummary += " → " + flights.get(flights.size() - 1).getDestinationAirport().getCode();
        dto.setRouteSummary(routeSummary);
        
        // Create airlines summary (assuming all flights are from the same airline for now)
        String airlinesSummary = flights.stream()
                .map(flight -> flight.getFlightNumber().substring(0, 2)) // Extract airline code
                .distinct()
                .collect(Collectors.joining(", "));
        dto.setAirlinesSummary(airlinesSummary);
        
        return dto;
    }
    
    private int getAvailableSeatsForClass(Flight flight, SeatClass seatClass) {
        return switch (seatClass) {
            case FIRST_CLASS -> flight.getFirstClassAvailableSeats();
            case BUSINESS_CLASS -> flight.getBusinessClassAvailableSeats();
            case ECONOMY_CLASS -> flight.getEconomyClassAvailableSeats();
        };
    }
    
    private AirportDto convertToAirportDto(Airport airport) {
        if (airport == null) return null;
        
        AirportDto dto = new AirportDto();
        dto.setId(airport.getId());
        dto.setCode(airport.getCode());
        dto.setName(airport.getName());
        dto.setCity(airport.getCity());
        dto.setCountry(airport.getCountry());
        dto.setLatitude(airport.getLatitude());
        dto.setLongitude(airport.getLongitude());
        dto.setTimeZone(airport.getTimeZone());
        return dto;
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