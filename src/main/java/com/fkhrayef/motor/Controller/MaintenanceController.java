package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.MaintenanceDTO;
import com.fkhrayef.motor.Model.Maintenance;
import com.fkhrayef.motor.Service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Invoice file management endpoints
    @PostMapping("/upload-invoice/{id}")
    public ResponseEntity<?> uploadInvoice(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("invoiceAmount") Double invoiceAmount) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invoice file is required"));
        }

        if (invoiceAmount == null || invoiceAmount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invoice amount is required and must be greater than 0"));
        }

        try {
            maintenanceService.uploadInvoice(id, file, invoiceAmount);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Invoice uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/download-invoice/{id}")
    public ResponseEntity<?> downloadInvoice(@PathVariable Integer id) {
        byte[] invoiceData = maintenanceService.downloadInvoice(id);
        
        // Get maintenance details to include service type in filename
        Maintenance maintenance = maintenanceService.getMaintenanceById(id);
        String serviceType = maintenance.getServiceType();
        
        // Clean service type for filename (remove special characters)
        String cleanServiceType = serviceType.replaceAll("[^a-zA-Z0-9\\s-]", "").replaceAll("\\s+", "-");
        String filename = String.format("%s-invoice-%d.pdf", cleanServiceType, id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(invoiceData);
    }

    @DeleteMapping("/delete-invoice/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Integer id) {
        maintenanceService.deleteInvoice(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Invoice deleted successfully"));
    }

}
