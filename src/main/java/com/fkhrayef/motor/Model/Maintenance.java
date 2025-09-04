package com.fkhrayef.motor.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // todo make pattern
    @NotEmpty(message = "recordType must not be Empty")
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
    @Column(columnDefinition = "int not null")
    private Integer mileage;

    private String notes;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
