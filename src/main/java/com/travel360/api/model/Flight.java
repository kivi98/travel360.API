package com.travel360.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", unique = true, nullable = false)
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "airplane_id", nullable = false)
    private Airplane airplane;

    @ManyToOne
    @JoinColumn(name = "origin_airport_id", nullable = false)
    private Airport originAirport;

    @ManyToOne
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destinationAirport;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Min(0)
    @Column(name = "first_class_price", nullable = false)
    private BigDecimal firstClassPrice;

    @Min(0)
    @Column(name = "business_class_price", nullable = false)
    private BigDecimal businessClassPrice;

    @Min(0)
    @Column(name = "economy_class_price", nullable = false)
    private BigDecimal economyClassPrice;

    @Column(name = "first_class_available_seats")
    private int firstClassAvailableSeats;

    @Column(name = "business_class_available_seats")
    private int businessClassAvailableSeats;

    @Column(name = "economy_class_available_seats")
    private int economyClassAvailableSeats;

    @Column(name = "distance_km")
    private int distanceKm;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.SCHEDULED;

    public boolean hasAvailableSeats(SeatClass seatClass) {
        return switch (seatClass) {
            case FIRST_CLASS -> firstClassAvailableSeats > 0;
            case BUSINESS_CLASS -> businessClassAvailableSeats > 0;
            case ECONOMY_CLASS -> economyClassAvailableSeats > 0;
        };
    }

    public void decrementAvailableSeats(SeatClass seatClass) {
        switch (seatClass) {
            case FIRST_CLASS -> firstClassAvailableSeats--;
            case BUSINESS_CLASS -> businessClassAvailableSeats--;
            case ECONOMY_CLASS -> economyClassAvailableSeats--;
        }
    }

    public BigDecimal getPriceForClass(SeatClass seatClass) {
        return switch (seatClass) {
            case FIRST_CLASS -> firstClassPrice;
            case BUSINESS_CLASS -> businessClassPrice;
            case ECONOMY_CLASS -> economyClassPrice;
        };
    }

    /**
     * Checks if this flight can connect with another flight, considering minimum connection time
     * @param nextFlight the connecting flight
     * @param minimumConnectionMinutes minimum connection time in minutes
     * @return true if connection is valid
     */
    public boolean canConnectTo(Flight nextFlight, int minimumConnectionMinutes) {
        if (!destinationAirport.getId().equals(nextFlight.getOriginAirport().getId())) {
            return false;
        }
        
        LocalDateTime minDepartureTime = arrivalTime.plusMinutes(minimumConnectionMinutes);
        return nextFlight.getDepartureTime().isAfter(minDepartureTime);
    }
} 