package com.fkhrayef.motor.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "varchar(255) not null")
    private String type;


    @Column(columnDefinition = "date not null")
    private LocalDate dueDate;

    @Column(columnDefinition = "varchar(255) not null")
    private String message;

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
