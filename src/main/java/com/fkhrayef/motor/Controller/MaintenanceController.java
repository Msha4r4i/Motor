package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.MaintenanceDTO;
import com.fkhrayef.motor.Service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/maintenances")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMaintenances() {
        return ResponseEntity.status(HttpStatus.OK).body(maintenanceService.getAllMaintenances());
    }

    @PostMapping("/add/{carId}")
    public ResponseEntity<?> addMaintenance(@PathVariable Integer carId, @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        maintenanceService.addMaintenance(carId, maintenanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Maintenance added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable Integer id, @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        maintenanceService.updateMaintenance(id, maintenanceDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Maintenance updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Integer id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/get/{carId}")
    public ResponseEntity<?> getMaintenancesByCarId(@PathVariable Integer carId) {
        return ResponseEntity.status(HttpStatus.OK).body(maintenanceService.getMaintenancesByCarId(carId));
    }
}
