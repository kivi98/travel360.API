package com.travel360.api.repository;

import com.travel360.api.model.Airplane;
import com.travel360.api.model.AirplaneSize;
import com.travel360.api.model.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    
    Optional<Airplane> findByRegistrationNumber(String registrationNumber);
    
    List<Airplane> findBySize(AirplaneSize size);
    
    List<Airplane> findByCurrentAirport(Airport airport);
    
    List<Airplane> findBySizeAndCurrentAirport(AirplaneSize size, Airport airport);
    
    @Query("SELECT a FROM Airplane a WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(a.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.model) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:size IS NULL OR a.size = :size) AND " +
           "(:airportId IS NULL OR a.currentAirport.id = :airportId)")
    Page<Airplane> findBySearchCriteria(Pageable pageable, 
                                       @Param("search") String search,
                                       @Param("size") AirplaneSize size,
                                       @Param("airportId") Long airportId);
} 