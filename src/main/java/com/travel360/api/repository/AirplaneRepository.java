package com.travel360.api.repository;

import com.travel360.api.model.Airplane;
import com.travel360.api.model.AirplaneSize;
import com.travel360.api.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    
    Optional<Airplane> findByRegistrationNumber(String registrationNumber);
    
    List<Airplane> findBySize(AirplaneSize size);
    
    List<Airplane> findByCurrentAirport(Airport airport);
    
    List<Airplane> findBySizeAndCurrentAirport(AirplaneSize size, Airport airport);
} 