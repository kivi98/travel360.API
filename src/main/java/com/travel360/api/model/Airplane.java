package com.travel360.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "airplanes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airplane extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String registrationNumber;

    @NotBlank
    @Size(max = 100)
    private String model;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AirplaneSize size;

    @Min(value = 1)
    @Column(name = "first_class_capacity")
    private int firstClassCapacity;

    @Min(value = 1)
    @Column(name = "business_class_capacity")
    private int businessClassCapacity;

    @Min(value = 1)
    @Column(name = "economy_class_capacity")
    private int economyClassCapacity;

    @ManyToOne
    @JoinColumn(name = "current_airport_id")
    private Airport currentAirport;

    @Column(name = "manufacturing_year")
    private int manufacturingYear;

    @Column(name = "max_range_km")
    private int maxRangeKm;

    public int getTotalCapacity() {
        return firstClassCapacity + businessClassCapacity + economyClassCapacity;
    }
} 