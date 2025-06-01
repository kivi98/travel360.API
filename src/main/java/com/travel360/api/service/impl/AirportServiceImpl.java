package com.travel360.api.service.impl;

import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.model.Airport;
import com.travel360.api.repository.AirportRepository;
import com.travel360.api.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirportServiceImpl implements AirportService {

    @Autowired
    private AirportRepository airportRepository;

    @Override
    public List<AirportDto> getAllAirports() {
        return airportRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<AirportDto> getAllAirports(Pageable pageable, String search) {
        Page<Airport> airportPage;
        
        if (search != null && !search.trim().isEmpty()) {
            airportPage = airportRepository.findBySearchCriteria(pageable, search.trim());
        } else {
            airportPage = airportRepository.findAll(pageable);
        }
        
        return airportPage.map(this::convertToDto);
    }

    @Override
    public Optional<AirportDto> getAirportById(Long id) {
        return airportRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<AirportDto> getAirportByCode(String code) {
        return airportRepository.findByCode(code.toUpperCase())
                .map(this::convertToDto);
    }

    @Override
    public List<AirportDto> getAirportsByCountry(String country) {
        return airportRepository.findByCountry(country).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AirportDto> getAirportsByCity(String city) {
        return airportRepository.findByCity(city).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public AirportDto createAirport(Airport airport) {
        if (existsByCode(airport.getCode())) {
            throw new RuntimeException("Airport with code " + airport.getCode() + " already exists");
        }
        
        airport.setCode(airport.getCode().toUpperCase());
        Airport savedAirport = airportRepository.save(airport);
        return convertToDto(savedAirport);
    }

    @Override
    public AirportDto updateAirport(Long id, Airport airport) {
        return airportRepository.findById(id)
                .map(existingAirport -> {
                    // Check if code is being changed and if new code already exists
                    if (!existingAirport.getCode().equals(airport.getCode().toUpperCase()) 
                        && existsByCode(airport.getCode())) {
                        throw new RuntimeException("Airport with code " + airport.getCode() + " already exists");
                    }
                    
                    existingAirport.setCode(airport.getCode().toUpperCase());
                    existingAirport.setName(airport.getName());
                    existingAirport.setCity(airport.getCity());
                    existingAirport.setCountry(airport.getCountry());
                    existingAirport.setLatitude(airport.getLatitude());
                    existingAirport.setLongitude(airport.getLongitude());
                    existingAirport.setTimeZone(airport.getTimeZone());
                    
                    Airport updatedAirport = airportRepository.save(existingAirport);
                    return convertToDto(updatedAirport);
                })
                .orElseThrow(() -> new RuntimeException("Airport not found with ID: " + id));
    }

    @Override
    public void deleteAirport(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new RuntimeException("Airport not found with ID: " + id);
        }
        airportRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return airportRepository.findByCode(code.toUpperCase()).isPresent();
    }

    private AirportDto convertToDto(Airport airport) {
        AirportDto dto = new AirportDto();
        dto.setId(airport.getId());
        dto.setCode(airport.getCode());
        dto.setName(airport.getName());
        dto.setCity(airport.getCity());
        dto.setCountry(airport.getCountry());
        dto.setLatitude(airport.getLatitude());
        dto.setLongitude(airport.getLongitude());
        dto.setTimeZone(airport.getTimeZone());
        return dto;
    }
} 