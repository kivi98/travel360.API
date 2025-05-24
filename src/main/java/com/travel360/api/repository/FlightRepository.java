package com.travel360.api.repository;

import com.travel360.api.model.Airplane;
import com.travel360.api.model.Airport;
import com.travel360.api.model.Flight;
import com.travel360.api.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    List<Flight> findByOriginAirportAndDestinationAirportAndDepartureTimeGreaterThanEqual(
            Airport originAirport, Airport destinationAirport, LocalDateTime departureTime);
    
    List<Flight> findByAirplaneAndDepartureTimeBetween(
            Airplane airplane, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT f FROM Flight f WHERE f.originAirport = :airport AND f.departureTime BETWEEN :startTime AND :endTime")
    List<Flight> findDepartingFlights(
            @Param("airport") Airport airport, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT f FROM Flight f WHERE f.destinationAirport = :airport AND f.arrivalTime BETWEEN :startTime AND :endTime")
    List<Flight> findArrivingFlights(
            @Param("airport") Airport airport, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT f FROM Flight f WHERE f.originAirport.id = :originId " +
           "AND f.destinationAirport.id = :destinationId " +
           "AND f.departureTime >= :departureTime " +
           "AND f.status IN ('SCHEDULED', 'BOARDING', 'DELAYED')")
    List<Flight> findAvailableDirectFlights(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("departureTime") LocalDateTime departureTime);
    
    @Query("SELECT f FROM Flight f WHERE f.status = :status")
    List<Flight> findByStatus(@Param("status") FlightStatus status);
} 