package com.fkhrayef.motor.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "mileage > 0")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "recordType must not be Empty")
    @Pattern(regexp = "^(MAINTENANCE|ACCIDENT)$", message = "Record type must be either 'MAINTENANCE' or 'ACCIDENT'")
    @Column(columnDefinition = "varchar(50) not null")
    private String recordType;

    @NotEmpty(message = "serviceType must not be Empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String serviceType;

    @NotNull(message = "serviceDate is required")
    @PastOrPresent(message = "serviceDate cannot be in the future")
    @Column(columnDefinition = "date")
    private LocalDate serviceDate;

    @NotNull(message = "mileage must be not null")
    @PositiveOrZero(message = "mileage must be greater than or equal to zero")
    @Column(columnDefinition = "int not null")
    private Integer mileage;

    private String notes;

    // Invoice
    @NotNull(message = "Invoice file URL cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String invoiceFileUrl;

    @NotNull(message = "Invoice amount cannot be null")
    @Column(columnDefinition = "DOUBLE NOT NULL")
    private Double invoiceAmount;

    // Relations
    @ManyToOne
    @JsonIgnore
    private Car car;

    // Timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
