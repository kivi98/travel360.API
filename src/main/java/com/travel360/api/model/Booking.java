package com.travel360.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingDetail> bookingDetails = new ArrayList<>();
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @PrePersist
    public void prePersist() {
        if (bookingReference == null) {
            bookingReference = generateBookingReference();
        }
        if (bookingDate == null) {
            bookingDate = LocalDateTime.now();
        }
        if (status == null) {
            status = BookingStatus.CONFIRMED;
        }
    }

    private String generateBookingReference() {
        return "B" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void addBookingDetail(BookingDetail bookingDetail) {
        bookingDetails.add(bookingDetail);
        bookingDetail.setBooking(this);
        recalculateTotalAmount();
    }

    public void removeBookingDetail(BookingDetail bookingDetail) {
        bookingDetails.remove(bookingDetail);
        bookingDetail.setBooking(null);
        recalculateTotalAmount();
    }

    private void recalculateTotalAmount() {
        totalAmount = bookingDetails.stream()
                .map(BookingDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}