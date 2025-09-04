package com.fkhrayef.motor.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Check(constraints = "role IN ('ADMIN','USER')")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Phone cannot be null")
    @Pattern(regexp = "^(\\+9665[0-9]\\d{8})$")
    @Column(columnDefinition = "VARCHAR(13) NOT NULL UNIQUE")
    private String phone;

    @NotEmpty(message = "Name cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @NotEmpty(message = "Email cannot be null")
    @Email(message = "Email is not valid")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL UNIQUE")
    private String email;

    @NotEmpty(message = "Password cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;

    @NotEmpty(message = "License file url cannot be null")
    @Column(columnDefinition = "VARCHAR(255)")
    private String licenseFileUrl;

    @NotNull(message = "License expiry date cannot be null")
    @Column(columnDefinition = "DATE")
    private LocalDate licenseExpiry;

    @Column(columnDefinition = "VARCHAR(20) NOT NULL")
    @Pattern(regexp = "^(ADMIN|USER)$")
    private String role;

    @Column(columnDefinition = "VARCHAR(100)")
    private String cardName;

    @Column(columnDefinition = "VARCHAR(32)")
    private String cardNumber;

    @Column(columnDefinition = "VARCHAR(8)")
    private String cardCvc;

    @Column(columnDefinition = "VARCHAR(4)")
    private String cardExpMonth;

    @Column(columnDefinition = "VARCHAR(6)")
    private String cardExpYear;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
