package com.fkhrayef.motor.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "mileage >= 0")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Make cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String make;

    @NotEmpty(message = "Model cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String model;

    @NotNull(message = "Year cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer year;

    @Column(columnDefinition = "VARCHAR(255)")
    private String nickname;

    @NotNull(message = "Mileage cannot be null")
    @PositiveOrZero(message = "mileage must be greater than or equal to zero")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer mileage;

    @NotEmpty(message = "VIN cannot be null")
    @Size(max = 17, message = "VIN must be 17 characters")
    @Column(columnDefinition = "VARCHAR(17) UNIQUE")
    private String vin;

    @NotNull(message = "Purchase date cannot be null")
    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate purchaseDate;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String registrationFileUrl;

    @Column(columnDefinition = "DATE")
    private LocalDate registrationExpiry;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String insuranceFileUrl;

    @Column(columnDefinition = "DATE")
    private LocalDate insuranceEndDate;

    // Relations
    @ManyToOne
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "car" )
    private Set<Maintenance> maintenances;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "car" )
    private Set<Reminder> reminders;

    // Timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
