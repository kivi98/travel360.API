package com.travel360.api.service;

import com.travel360.api.dto.booking.BookingRequest;
import com.travel360.api.dto.booking.BookingResponse;
import com.travel360.api.model.Booking;
import com.travel360.api.model.BookingStatus;
import com.travel360.api.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    
    BookingResponse createBooking(BookingRequest bookingRequest, User user);
    
    BookingResponse createBookingForCustomer(BookingRequest bookingRequest, Long customerId, User operator);
    
    Optional<BookingResponse> getBookingById(Long id);
    
    Optional<BookingResponse> getBookingByReference(String bookingReference);
    
    List<BookingResponse> getBookingsByUser(User user);
    
    List<BookingResponse> getAllBookings();
    
    List<BookingResponse> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<BookingResponse> getBookingsByFlight(Long flightId);
    
    List<BookingResponse> searchBookingsByPassenger(String passengerName);
    
    BookingResponse updateBookingStatus(Long id, BookingStatus status);
    
    void cancelBooking(Long id);
    
    void deleteBooking(Long id);
    
    byte[] generateBookingConfirmation(Long bookingId) throws Exception;
    
    byte[] generatePassengerManifest(Long flightId) throws Exception;
} 