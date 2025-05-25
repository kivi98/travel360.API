package com.travel360.api.controller;

import com.travel360.api.dto.booking.BookingRequest;
import com.travel360.api.dto.booking.BookingResponse;
import com.travel360.api.model.BookingStatus;
import com.travel360.api.model.User;
import com.travel360.api.service.BookingService;
import com.travel360.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Management", description = "APIs for managing flight bookings, reservations, and related documents")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.getUserByUsername(userDetails.getUsername()).orElseThrow();
        BookingResponse booking = bookingService.createBooking(bookingRequest, user);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @PostMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<BookingResponse> createBookingForCustomer(
            @Valid @RequestBody BookingRequest bookingRequest,
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User operator = userService.getUserByUsername(userDetails.getUsername()).orElseThrow();
        BookingResponse booking = bookingService.createBookingForCustomer(bookingRequest, customerId, operator);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingResponse> getBookingByReference(@PathVariable String reference) {
        return bookingService.getBookingByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername()).orElseThrow();
        List<BookingResponse> bookings = bookingService.getBookingsByUser(user);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<List<BookingResponse>> getBookingsByDateRange(
            @RequestParam LocalDateTime startDate, 
            @RequestParam LocalDateTime endDate) {
        
        return ResponseEntity.ok(bookingService.getBookingsByDateRange(startDate, endDate));
    }

    @GetMapping("/flight/{flightId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<List<BookingResponse>> getBookingsByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(bookingService.getBookingsByFlight(flightId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<List<BookingResponse>> searchBookingsByPassenger(@RequestParam String passengerName) {
        return ResponseEntity.ok(bookingService.searchBookingsByPassenger(passengerName));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id, 
            @RequestBody BookingStatus status) {
        
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/documents/{id}/confirmation")
    public ResponseEntity<byte[]> generateBookingConfirmation(@PathVariable Long id) {
        try {
            byte[] document = bookingService.generateBookingConfirmation(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=booking-confirmation.pdf")
                    .body(document);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/documents/flight/{id}/manifest")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    public ResponseEntity<byte[]> generatePassengerManifest(@PathVariable Long id) {
        try {
            byte[] document = bookingService.generatePassengerManifest(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=passenger-manifest.pdf")
                    .body(document);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 