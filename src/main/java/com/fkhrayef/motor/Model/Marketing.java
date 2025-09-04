package com.fkhrayef.motor.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Title can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String title;

    @NotEmpty(message = "Offer type can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String offerType;

    @NotEmpty(message = "Description can't be null")
    @Column(columnDefinition = "text not null")
    private String description;

    @NotEmpty(message = "Poster url can't be null")
    @Column(columnDefinition = "varchar(4069) not null")
    @Size(max = 4069)
    private String posterUrl;

    @NotNull(message = "Start date can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime startDate;

    @NotNull(message = "End date can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
