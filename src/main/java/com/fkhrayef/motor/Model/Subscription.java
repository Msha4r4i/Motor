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
public class Subscription {

    //  id int [pk, increment]
    //  user_id int [ref: > Users.id]
    //  plan varchar // Free (default if none), Pro, Enterprise
    //  start_date datetime
    //  end_date datetime
    //  created_at datetime
    //  updated_at datetime

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "user_id can't be null")
    @Column(columnDefinition = "int not null")
    private Integer user_id;
    @NotEmpty(message = "plan can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String plan = "Free";
    @NotNull(message = "start_date can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime start_date;
    @NotNull(message = "end_date can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime end_date;
    @NotNull(message = "createdAt can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime createdAt;
    @NotNull(message = "updatedAt can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime updatedAt;



}
