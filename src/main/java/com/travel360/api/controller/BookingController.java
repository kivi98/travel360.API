package com.travel360.api.controller;

import com.travel360.api.dto.booking.BookingRequest;
import com.travel360.api.dto.booking.BookingResponse;
import com.travel360.api.dto.common.ApiResponse;
import com.travel360.api.model.BookingStatus;
import com.travel360.api.model.User;
import com.travel360.api.service.BookingService;
import com.travel360.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
        summary = "Create new booking",
        description = "Create a new flight booking for the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Booking created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid booking data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Parameter(description = "Booking details", required = true)
            @Valid @RequestBody BookingRequest bookingRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByUsername(userDetails.getUsername()).orElseThrow();
            BookingResponse booking = bookingService.createBooking(bookingRequest, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Booking created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to create booking"));
        }
    }

    @PostMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Create booking for customer",
        description = "Create a new flight booking for a specific customer. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<BookingResponse>> createBookingForCustomer(
            @Parameter(description = "Booking details", required = true)
            @Valid @RequestBody BookingRequest bookingRequest,
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User operator = userService.getUserByUsername(userDetails.getUsername()).orElseThrow();
            BookingResponse booking = bookingService.createBookingForCustomer(bookingRequest, customerId, operator);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Booking created successfully for customer"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to create booking for customer"));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get booking by ID",
        description = "Retrieve a specific booking by its ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> ResponseEntity.ok(ApiResponse.success(booking, "Booking found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Booking not found with ID: " + id)));
    }

    @GetMapping("/reference/{reference}")
    @Operation(
        summary = "Get booking by reference",
        description = "Retrieve a booking by its reference number",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByReference(
        @Parameter(description = "Booking reference", required = true)
        @PathVariable String reference) {
        return bookingService.getBookingByReference(reference)
                .map(booking -> ResponseEntity.ok(ApiResponse.success(booking, "Booking found")))
                .orElse(ResponseEntity.status(404)
                    .body(ApiResponse.error("Booking not found with reference: " + reference)));
    }

    @GetMapping("/user")
    @Operation(
        summary = "Get user's bookings",
        description = "Retrieve all bookings for the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings(
        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByUsername(userDetails.getUsername()).orElseThrow();
            List<BookingResponse> bookings = bookingService.getBookingsByUser(user);
            String message = bookings.isEmpty() ? 
                "No bookings found for user" : 
                "Found " + bookings.size() + " bookings";
            return ResponseEntity.ok(ApiResponse.success(bookings, message));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve user bookings", "Internal server error"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Get all bookings",
        description = "Retrieve all bookings in the system. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        try {
            List<BookingResponse> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(ApiResponse.success(bookings, "Retrieved " + bookings.size() + " bookings"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve bookings", "Internal server error"));
        }
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Get bookings by date range",
        description = "Retrieve bookings within a specific date range. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByDateRange(
            @Parameter(description = "Start date", required = true)
            @RequestParam LocalDateTime startDate, 
            @Parameter(description = "End date", required = true)
            @RequestParam LocalDateTime endDate) {
        try {
            List<BookingResponse> bookings = bookingService.getBookingsByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(bookings, 
                "Found " + bookings.size() + " bookings in the specified date range"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to retrieve bookings by date range"));
        }
    }

    @GetMapping("/flight/{flightId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Get bookings by flight",
        description = "Retrieve all bookings for a specific flight. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByFlight(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long flightId) {
        try {
            List<BookingResponse> bookings = bookingService.getBookingsByFlight(flightId);
            return ResponseEntity.ok(ApiResponse.success(bookings, 
                "Found " + bookings.size() + " bookings for flight"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve bookings for flight", "Internal server error"));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Search bookings by passenger",
        description = "Search for bookings by passenger name. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<BookingResponse>>> searchBookingsByPassenger(
        @Parameter(description = "Passenger name to search", required = true)
        @RequestParam String passengerName) {
        try {
            List<BookingResponse> bookings = bookingService.searchBookingsByPassenger(passengerName);
            return ResponseEntity.ok(ApiResponse.success(bookings, 
                "Found " + bookings.size() + " bookings matching passenger name"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to search bookings", "Search failed"));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
    @Operation(
        summary = "Update booking status",
        description = "Update the status of a booking. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(
            @Parameter(description = "Booking ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "New booking status", required = true)
            @RequestBody BookingStatus status) {
        try {
            BookingResponse booking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(booking, "Booking status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to update booking status"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Cancel booking",
        description = "Cancel a booking",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "Failed to cancel booking"));
        }
    }

    @GetMapping("/documents/{id}/confirmation")
    @Operation(
        summary = "Generate booking confirmation",
        description = "Generate a PDF booking confirmation document",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<byte[]> generateBookingConfirmation(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable Long id) {
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
    @Operation(
        summary = "Generate passenger manifest",
        description = "Generate a PDF passenger manifest for a flight. Requires OPERATOR or ADMINISTRATOR role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<byte[]> generatePassengerManifest(
        @Parameter(description = "Flight ID", required = true)
        @PathVariable Long id) {
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