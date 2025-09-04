package com.fkhrayef.motor.DTOin;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CarDTO {
    @NotEmpty(message = "Make cannot be null")
    private String make;

    @NotEmpty(message = "Model cannot be null")
    private String model;

    @NotNull(message = "Year cannot be null")
    @Positive(message = "Year must be greater than zero")
    private Integer year;

    @NotEmpty(message = "Nickname cannot be null")
    private String nickname;

    @NotNull(message = "Mileage cannot be null")
    @PositiveOrZero(message = "mileage must be greater than or equal to zero")
    private Integer mileage;

    @NotEmpty(message = "VIN cannot be null")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    private String vin;

    @NotNull(message = "Purchase date cannot be null")
    private LocalDate purchaseDate;
}
