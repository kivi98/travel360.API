package com.travel360.api.repository;

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
public interface AirportRepository extends JpaRepository<Airport, Long> {
    
    Optional<Airport> findByCode(String code);
    
    List<Airport> findByCountry(String country);
    
    List<Airport> findByCity(String city);
    
    @Query("SELECT a FROM Airport a WHERE " +
           "LOWER(a.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.country) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Airport> findBySearchCriteria(Pageable pageable, @Param("search") String search);
} 