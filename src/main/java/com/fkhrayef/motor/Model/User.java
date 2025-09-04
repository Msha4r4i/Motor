package com.fkhrayef.motor.Model;

import jakarta.persistence.*;
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
import java.util.Set;

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

    @Column(columnDefinition = "VARCHAR(13) NOT NULL UNIQUE")
    private String phone;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL UNIQUE")
    private String email;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;

    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role must be either 'ADMIN' or 'USER'")
    private String role;

    // License Information (Optional)
    @Column(columnDefinition = "VARCHAR(255)")
    private String licenseFileUrl;
    @Column(columnDefinition = "DATE")
    private LocalDate licenseExpiry;

    // Card Information (Optional but required for subscription)
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

    // Relations
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Car> cars;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Subscription> subscription;

    // Timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
