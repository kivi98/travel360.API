package com.travel360.api.dto.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    
    @NotEmpty
    @Valid
    private List<PassengerDetail> passengers;
    
    private String contactEmail;
    
    private String contactPhone;
    
    @Data
    public static class PassengerDetail {
        @Size(min = 2, max = 100)
        private String passengerName;
        
        private String passportNumber;
        
        private Long flightId;
        
        private String seatClass;
        
        private String specialRequirements;
        
        private Long connectingFlightDetailId;
    }
} 