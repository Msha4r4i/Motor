package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.CarDTO;
import com.fkhrayef.motor.Service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCars() {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getAllCars());
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addCar(@PathVariable Integer userId, @Valid @RequestBody CarDTO carDTO) {
        carService.addCar(userId, carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Car added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCar(@PathVariable Integer id, @Valid @RequestBody CarDTO carDTO) {
        carService.updateCar(id, carDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Car updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
