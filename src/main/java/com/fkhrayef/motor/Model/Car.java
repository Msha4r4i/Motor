package com.fkhrayef.motor.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String make;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String model;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer year;

    @Column(columnDefinition = "VARCHAR(255)")
    private String nickname;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer mileage;

    @NotEmpty(message = "VIN cannot be null")
    @Column(columnDefinition = "VARCHAR(255) UNIQUE")
    private String vin;

    @Column(columnDefinition = "DATETIME NOT NULL")
    private LocalDate purchaseDate;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String registrationFileUrl;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime registrationExpiry;

    @Column(columnDefinition = "VARCHAR(255)")
    private String insuranceFileUrl;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime insuranceEndDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
