package com.travel360.api.repository;

import com.travel360.api.model.Booking;
import com.travel360.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByUser(User user);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate")
    List<Booking> findByBookingDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Booking b JOIN b.bookingDetails bd WHERE bd.flight.id = :flightId")
    List<Booking> findByFlightId(@Param("flightId") Long flightId);
    
    @Query("SELECT DISTINCT b FROM Booking b JOIN b.bookingDetails bd " +
           "WHERE bd.passengerName LIKE %:passengerName% OR b.user.firstName LIKE %:passengerName%")
    List<Booking> findByPassengerNameOrUserName(@Param("passengerName") String passengerName);
}