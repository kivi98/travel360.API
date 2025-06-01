package com.travel360.api.service;

import com.travel360.api.dto.flight.AirplaneDto;
import com.travel360.api.model.Airplane;
import com.travel360.api.model.AirplaneSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AirplaneService {
    
    List<AirplaneDto> getAllAirplanes();
    
    Page<AirplaneDto> getAllAirplanes(Pageable pageable, String search, AirplaneSize size, Long airportId);
    
    Optional<AirplaneDto> getAirplaneById(Long id);
    
    Optional<AirplaneDto> getAirplaneByRegistrationNumber(String registrationNumber);
    
    List<AirplaneDto> getAirplanesBySize(AirplaneSize size);
    
    List<AirplaneDto> getAirplanesByCurrentAirport(Long airportId);
    
    List<AirplaneDto> getAirplanesBySizeAndCurrentAirport(AirplaneSize size, Long airportId);
    
    AirplaneDto createAirplane(Airplane airplane);
    
    AirplaneDto updateAirplane(Long id, Airplane airplane);
    
    void deleteAirplane(Long id);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    AirplaneDto updateCurrentAirport(Long airplaneId, Long airportId);
} 