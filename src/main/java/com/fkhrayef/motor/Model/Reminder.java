package com.fkhrayef.motor.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reminder {


    //id int [pk, increment]
    //  car_id int [ref: > Cars.id]
    //  user_id int [ref: > Users.id]
    //  type varchar // license_expiry, insurance_expiry, registration_expiry, maintenance
    //  due_date datetime
    //  message text
    //  is_sent boolean
    //  created_at datetime
    //  updated_at datetime

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "user_id can't be null")
    @Column(columnDefinition = "int not null")
    private Integer user_id;
    @NotNull(message = "car_id can't be null")
    @Column(columnDefinition = "int not null")
    private Integer car_id;
    @NotEmpty(message = "type can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String type;
    @NotNull(message = "due_date can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime due_date;
    @NotEmpty(message = "message can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String message;
    @NotNull(message = "is_sent can't be null")
    @Column(columnDefinition = "boolean not null")
    private Boolean is_sent;
    @NotNull(message = "createdAt can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime createdAt;
    @NotNull(message = "updatedAt can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime updatedAt;

}
