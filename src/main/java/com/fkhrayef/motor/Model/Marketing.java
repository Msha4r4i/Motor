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

public class Marketing {

    //  id int [pk, increment]
    //  title varchar
    //  offer_type varchar // accessories, insurance, services
    //  description text
    //  poster_url varchar
    //  start_date datetime
    //  end_date datetime
    //  created_at datetime
    //  updated_at datetime

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "title can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String title;

    @NotEmpty(message = "offer_type can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String offer_type;


    @NotEmpty(message = "description can't be null")
    @Column(columnDefinition = "text not null")
    private String description;


    @NotEmpty(message = "poster_url can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String poster_url;


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
