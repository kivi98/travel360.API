package com.travel360.api.dto.flight;

import lombok.Data;

@Data
public class AirportDto {
    
    private Long id;
    private String code;
    private String name;
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private String timeZone;
}