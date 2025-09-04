package com.fkhrayef.motor.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotEmpty(message = "Type can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String type;

    @NotNull(message = "Due date can't be null")
    @Column(columnDefinition = "date not null")
    private LocalDateTime dueDate;

    @NotEmpty(message = "Message can't be null")
    @Column(columnDefinition = "varchar(255) not null")
    private String message;

    @NotNull(message = "isSent can't be null")
    @Column(columnDefinition = "boolean not null")
    private Boolean isSent;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
