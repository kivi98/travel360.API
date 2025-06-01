package com.travel360.api.service;

import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.model.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AirportService {
    
    List<AirportDto> getAllAirports();
    
    Page<AirportDto> getAllAirports(Pageable pageable, String search);
    
    Optional<AirportDto> getAirportById(Long id);
    
    Optional<AirportDto> getAirportByCode(String code);
    
    List<AirportDto> getAirportsByCountry(String country);
    
    List<AirportDto> getAirportsByCity(String city);
    
    AirportDto createAirport(Airport airport);
    
    AirportDto updateAirport(Long id, Airport airport);
    
    void deleteAirport(Long id);
    
    boolean existsByCode(String code);
} 