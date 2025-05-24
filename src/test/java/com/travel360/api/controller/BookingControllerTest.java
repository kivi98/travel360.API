package com.travel360.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel360.api.dto.booking.BookingRequest;
import com.travel360.api.dto.booking.BookingResponse;
import com.travel360.api.model.BookingStatus;
import com.travel360.api.model.SeatClass;
import com.travel360.api.model.User;
import com.travel360.api.service.BookingService;
import com.travel360.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;
    
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    public void testCreateBooking() throws Exception {
        // Prepare test data
        BookingRequest bookingRequest = new BookingRequest();
        List<BookingRequest.PassengerDetail> passengers = new ArrayList<>();
        
        BookingRequest.PassengerDetail passenger = new BookingRequest.PassengerDetail();
        passenger.setPassengerName("John Doe");
        passenger.setPassportNumber("ABC12345");
        passenger.setFlightId(1L);
        passenger.setSeatClass("ECONOMY_CLASS");
        passengers.add(passenger);
        
        bookingRequest.setPassengers(passengers);
        bookingRequest.setContactEmail("john@example.com");
        bookingRequest.setContactPhone("1234567890");

        BookingResponse bookingResponse = createMockBookingResponse();
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("customer");

        // Mock service behavior
        when(userService.getUserByUsername("customer")).thenReturn(Optional.of(mockUser));
        when(bookingService.createBooking(any(BookingRequest.class), any(User.class))).thenReturn(bookingResponse);

        // Perform the test
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingReference").value("B12345678"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    public void testGetBookingById() throws Exception {
        // Mock service behavior
        when(bookingService.getBookingById(1L)).thenReturn(Optional.of(createMockBookingResponse()));

        // Perform the test
        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingReference").value("B12345678"));
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    public void testGetNonExistentBooking() throws Exception {
        // Mock service behavior
        when(bookingService.getBookingById(999L)).thenReturn(Optional.empty());

        // Perform the test
        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "operator", roles = "OPERATOR")
    public void testUpdateBookingStatus() throws Exception {
        // Mock service behavior
        BookingResponse updatedBooking = createMockBookingResponse();
        updatedBooking.setStatus(BookingStatus.CANCELLED);
        
        when(bookingService.updateBookingStatus(eq(1L), eq(BookingStatus.CANCELLED))).thenReturn(updatedBooking);

        // Perform the test
        mockMvc.perform(put("/api/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CANCELLED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    public void testCancelOwnBooking() throws Exception {
        // Perform the test for a customer cancelling their own booking
        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isOk());
    }

    private BookingResponse createMockBookingResponse() {
        BookingResponse response = new BookingResponse();
        response.setId(1L);
        response.setBookingReference("B12345678");
        response.setBookingDate(LocalDateTime.now());
        response.setTotalAmount(new BigDecimal("599.99"));
        response.setStatus(BookingStatus.CONFIRMED);
        response.setContactEmail("john@example.com");
        response.setContactPhone("1234567890");
        response.setCustomerName("John Doe");
        
        BookingResponse.BookingDetailDto detail = new BookingResponse.BookingDetailDto();
        detail.setId(1L);
        detail.setPassengerName("John Doe");
        detail.setPassportNumber("ABC12345");
        detail.setSeatClass(SeatClass.ECONOMY_CLASS);
        detail.setSeatNumber("23A");
        detail.setAmount(new BigDecimal("599.99"));
        
        response.setBookingDetails(Collections.singletonList(detail));
        
        return response;
    }
} 