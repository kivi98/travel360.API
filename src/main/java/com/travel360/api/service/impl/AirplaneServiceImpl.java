package com.travel360.api.service.impl;

import com.travel360.api.dto.flight.AirplaneDto;
import com.travel360.api.dto.flight.AirportDto;
import com.travel360.api.model.Airplane;
import com.travel360.api.model.AirplaneSize;
import com.travel360.api.model.Airport;
import com.travel360.api.repository.AirplaneRepository;
import com.travel360.api.repository.AirportRepository;
import com.travel360.api.service.AirplaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirplaneServiceImpl implements AirplaneService {

    @Autowired
    private AirplaneRepository airplaneRepository;
    
    @Autowired
    private AirportRepository airportRepository;

    @Override
    public List<AirplaneDto> getAllAirplanes() {
        return airplaneRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<AirplaneDto> getAllAirplanes(Pageable pageable, String search, AirplaneSize size, Long airportId) {
        Page<Airplane> airplanePage = airplaneRepository.findBySearchCriteria(pageable, search, size, airportId);
        return airplanePage.map(this::convertToDto);
    }

    @Override
    public Optional<AirplaneDto> getAirplaneById(Long id) {
        return airplaneRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<AirplaneDto> getAirplaneByRegistrationNumber(String registrationNumber) {
        return airplaneRepository.findByRegistrationNumber(registrationNumber.toUpperCase())
                .map(this::convertToDto);
    }

    @Override
    public List<AirplaneDto> getAirplanesBySize(AirplaneSize size) {
        return airplaneRepository.findBySize(size).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AirplaneDto> getAirplanesByCurrentAirport(Long airportId) {
        return airportRepository.findById(airportId)
                .map(airport -> airplaneRepository.findByCurrentAirport(airport).stream()
                        .map(this::convertToDto)
                        .toList())
                .orElseThrow(() -> new RuntimeException("Airport not found with ID: " + airportId));
    }

    @Override
    public List<AirplaneDto> getAirplanesBySizeAndCurrentAirport(AirplaneSize size, Long airportId) {
        return airportRepository.findById(airportId)
                .map(airport -> airplaneRepository.findBySizeAndCurrentAirport(size, airport).stream()
                        .map(this::convertToDto)
                        .toList())
                .orElseThrow(() -> new RuntimeException("Airport not found with ID: " + airportId));
    }

    @Override
    public AirplaneDto createAirplane(Airplane airplane) {
        if (existsByRegistrationNumber(airplane.getRegistrationNumber())) {
            throw new RuntimeException("Airplane with registration number " + airplane.getRegistrationNumber() + " already exists");
        }
        
        airplane.setRegistrationNumber(airplane.getRegistrationNumber().toUpperCase());
        
        // Validate current airport if provided
        if (airplane.getCurrentAirport() != null && airplane.getCurrentAirport().getId() != null) {
            Airport airport = airportRepository.findById(airplane.getCurrentAirport().getId())
                    .orElseThrow(() -> new RuntimeException("Airport not found with ID: " + airplane.getCurrentAirport().getId()));
            airplane.setCurrentAirport(airport);
        }
        
        Airplane savedAirplane = airplaneRepository.save(airplane);
        return convertToDto(savedAirplane);
    }

    @Override
    public AirplaneDto updateAirplane(Long id, Airplane airplane) {
        return airplaneRepository.findById(id)
                .map(existingAirplane -> {
                    // Check if registration number is being changed and if new registration already exists
                    if (!existingAirplane.getRegistrationNumber().equals(airplane.getRegistrationNumber().toUpperCase()) 
                        && existsByRegistrationNumber(airplane.getRegistrationNumber())) {
                        throw new RuntimeException("Airplane with registration number " + airplane.getRegistrationNumber() + " already exists");
                    }
                    
                    existingAirplane.setRegistrationNumber(airplane.getRegistrationNumber().toUpperCase());
                    existingAirplane.setModel(airplane.getModel());
                    existingAirplane.setSize(airplane.getSize());
                    existingAirplane.setFirstClassCapacity(airplane.getFirstClassCapacity());
                    existingAirplane.setBusinessClassCapacity(airplane.getBusinessClassCapacity());
                    existingAirplane.setEconomyClassCapacity(airplane.getEconomyClassCapacity());
                    existingAirplane.setManufacturingYear(airplane.getManufacturingYear());
                    existingAirplane.setMaxRangeKm(airplane.getMaxRangeKm());
                    
                    // Update current airport if provided
                    if (airplane.getCurrentAirport() != null && airplane.getCurrentAirport().getId() != null) {
                        Airport airport = airportRepository.findById(airplane.getCurrentAirport().getId())
                                .orElseThrow(() -> new RuntimeException("Airport not found with ID: " + airplane.getCurrentAirport().getId()));
                        existingAirplane.setCurrentAirport(airport);
                    } else {
                        existingAirplane.setCurrentAirport(null);
                    }
                    
                    Airplane updatedAirplane = airplaneRepository.save(existingAirplane);
                    return convertToDto(updatedAirplane);
                })
                .orElseThrow(() -> new RuntimeException("Airplane not found with ID: " + id));
    }

    @Override
    public void deleteAirplane(Long id) {
        if (!airplaneRepository.existsById(id)) {
            throw new RuntimeException("Airplane not found with ID: " + id);
        }
        airplaneRepository.deleteById(id);
    }

    @Override
    public boolean existsByRegistrationNumber(String registrationNumber) {
        return airplaneRepository.findByRegistrationNumber(registrationNumber.toUpperCase()).isPresent();
    }

    @Override
    public AirplaneDto updateCurrentAirport(Long airplaneId, Long airportId) {
        Airplane airplane = airplaneRepository.findById(airplaneId)
                .orElseThrow(() -> new RuntimeException("Airplane not found with ID: " + airplaneId));
        
        Airport airport = null;
        if (airportId != null) {
            airport = airportRepository.findById(airportId)
                    .orElseThrow(() -> new RuntimeException("Airport not found with ID: " + airportId));
        }
        
        airplane.setCurrentAirport(airport);
        Airplane updatedAirplane = airplaneRepository.save(airplane);
        return convertToDto(updatedAirplane);
    }

    private AirplaneDto convertToDto(Airplane airplane) {
        AirplaneDto dto = new AirplaneDto();
        dto.setId(airplane.getId());
        dto.setRegistrationNumber(airplane.getRegistrationNumber());
        dto.setModel(airplane.getModel());
        dto.setSize(airplane.getSize());
        dto.setFirstClassCapacity(airplane.getFirstClassCapacity());
        dto.setBusinessClassCapacity(airplane.getBusinessClassCapacity());
        dto.setEconomyClassCapacity(airplane.getEconomyClassCapacity());
        dto.setTotalCapacity(airplane.getTotalCapacity());
        dto.setManufacturingYear(airplane.getManufacturingYear());
        dto.setMaxRangeKm(airplane.getMaxRangeKm());
        dto.setActive(airplane.isActive());
        
        // Convert current airport to DTO if present
        if (airplane.getCurrentAirport() != null) {
            AirportDto airportDto = new AirportDto();
            airportDto.setId(airplane.getCurrentAirport().getId());
            airportDto.setCode(airplane.getCurrentAirport().getCode());
            airportDto.setName(airplane.getCurrentAirport().getName());
            airportDto.setCity(airplane.getCurrentAirport().getCity());
            airportDto.setCountry(airplane.getCurrentAirport().getCountry());
            airportDto.setLatitude(airplane.getCurrentAirport().getLatitude());
            airportDto.setLongitude(airplane.getCurrentAirport().getLongitude());
            airportDto.setTimeZone(airplane.getCurrentAirport().getTimeZone());
            dto.setCurrentAirport(airportDto);
        }
        
        return dto;
    }
} 