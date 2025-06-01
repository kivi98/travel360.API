package com.travel360.api.dto.flight;

import lombok.Data;

import java.util.List;

@Data
public class FlightSearchResponse {
    
    private List<DirectFlightDto> directFlights;
    private List<TransitFlightDto> transitFlights;
    private int totalDirectFlights;
    private int totalTransitFlights;
    private String searchSummary;
    
    public FlightSearchResponse() {
        this.directFlights = List.of();
        this.transitFlights = List.of();
        this.totalDirectFlights = 0;
        this.totalTransitFlights = 0;
    }
    
    public FlightSearchResponse(List<DirectFlightDto> directFlights, List<TransitFlightDto> transitFlights) {
        this.directFlights = directFlights != null ? directFlights : List.of();
        this.transitFlights = transitFlights != null ? transitFlights : List.of();
        this.totalDirectFlights = this.directFlights.size();
        this.totalTransitFlights = this.transitFlights.size();
        
        if (this.totalDirectFlights > 0 && this.totalTransitFlights > 0) {
            this.searchSummary = String.format("Found %d direct flights and %d transit flight options", 
                this.totalDirectFlights, this.totalTransitFlights);
        } else if (this.totalDirectFlights > 0) {
            this.searchSummary = String.format("Found %d direct flights", this.totalDirectFlights);
        } else if (this.totalTransitFlights > 0) {
            this.searchSummary = String.format("Found %d transit flight options", this.totalTransitFlights);
        } else {
            this.searchSummary = "No flights found for the specified criteria";
        }
    }
} 