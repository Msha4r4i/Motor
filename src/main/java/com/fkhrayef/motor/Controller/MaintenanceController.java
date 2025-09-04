package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.MaintenanceDTO;
import com.fkhrayef.motor.Model.Maintenance;
import com.fkhrayef.motor.Service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/maintenances")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllMaintenance() {
        List<Maintenance> list = maintenanceService.getAllMaintenance();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<?> addMaintenance(@PathVariable Integer id, @Valid @RequestBody MaintenanceDTO dto) {
        maintenanceService.addMaintenance(id, dto);
        return ResponseEntity.ok(new ApiResponse("Maintenance added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable Integer id, @Valid @RequestBody MaintenanceDTO dto) {
        maintenanceService.updateMaintenance(id, dto);
        return ResponseEntity.ok(new ApiResponse("Maintenance updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Integer id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.ok(new ApiResponse("Maintenance deleted"));
    }
}
