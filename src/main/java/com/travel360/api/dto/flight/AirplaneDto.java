package com.travel360.api.dto.flight;

import com.travel360.api.model.AirplaneSize;
import lombok.Data;

@Data
public class AirplaneDto {
    
    private Long id;
    private String registrationNumber;
    private String model;
    private AirplaneSize size;
    private int firstClassCapacity;
    private int businessClassCapacity;
    private int economyClassCapacity;
    private int totalCapacity;
    private AirportDto currentAirport;
    private int manufacturingYear;
    private int maxRangeKm;
    private boolean active;
} 