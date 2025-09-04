package com.fkhrayef.motor.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Check(constraints = "type IN ('license_expiry','insurance_expiry','registration_expiry','maintenance')")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Type can't be null")
    @Pattern(regexp = "^(license_expiry|insurance_expiry|registration_expiry|maintenance)$", message = "Type must be one of: license_expiry, insurance_expiry, registration_expiry, maintenance")
    @Column(columnDefinition = "varchar(255) not null")
    private String type;

    @NotNull(message = "Due date can't be null")
    @FutureOrPresent(message = "Due date cannot be in the past")
    @Column(columnDefinition = "date not null")
    private LocalDate dueDate;

    @NotEmpty(message = "Message can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String message;

    @NotNull(message = "isSent can't be null")
    @Column(columnDefinition = "boolean not null")
    private Boolean isSent;

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
