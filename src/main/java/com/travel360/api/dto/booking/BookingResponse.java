package com.travel360.api.dto.booking;

import com.travel360.api.dto.flight.FlightDto;
import com.travel360.api.model.BookingStatus;
import com.travel360.api.model.SeatClass;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {
    
    private Long id;
    private String bookingReference;
    private LocalDateTime bookingDate;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private String contactEmail;
    private String contactPhone;
    private String customerName;
    private List<BookingDetailDto> bookingDetails;
    
    @Data
    public static class BookingDetailDto {
        private Long id;
        private FlightDto flight;
        private String passengerName;
        private String passportNumber;
        private SeatClass seatClass;
        private String seatNumber;
        private BigDecimal amount;
        private boolean checkedIn;
        private boolean transit;
        private String specialRequirements;
    }
} 