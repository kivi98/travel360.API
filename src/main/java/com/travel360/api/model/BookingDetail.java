package com.travel360.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "passport_number")
    private String passportNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", nullable = false)
    private SeatClass seatClass;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "checked_in")
    private boolean checkedIn = false;

    @Column(name = "is_transit")
    private boolean transit = false;

    @Column(name = "special_requirements")
    private String specialRequirements;

    @ManyToOne
    @JoinColumn(name = "connecting_booking_detail_id")
    private BookingDetail connectingBookingDetail;
} 