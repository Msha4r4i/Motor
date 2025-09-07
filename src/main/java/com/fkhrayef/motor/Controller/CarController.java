package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.CarDTO;
import com.fkhrayef.motor.DTOin.CarMileageUpdateDTO;
import com.fkhrayef.motor.Service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

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

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getCarsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getCarsByUserId(userId));
    }

    // Registration file management endpoints
    @PostMapping("/upload-registration/{id}")
    public ResponseEntity<?> uploadRegistration(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("registrationExpiry") String registrationExpiry) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Registration file is required"));
        }

        if (registrationExpiry == null || registrationExpiry.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Registration expiry date is required"));
        }

        try {
            LocalDate expiryDate = LocalDate.parse(registrationExpiry);
            carService.uploadRegistration(id, file, expiryDate);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Registration uploaded successfully"));
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invalid date format. Please use yyyy-MM-dd format"));
        }
    }

    @GetMapping("/download-registration/{id}")
    public ResponseEntity<?> downloadRegistration(@PathVariable Integer id) {
        byte[] registrationData = carService.downloadRegistration(id);
        String filename = String.format("car-%d-registration.pdf", id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(registrationData);
    }

    @DeleteMapping("/delete-registration/{id}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Integer id) {
        carService.deleteRegistration(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Registration deleted successfully"));
    }

    // Insurance file management endpoints
    @PostMapping("/upload-insurance/{id}")
    public ResponseEntity<?> uploadInsurance(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("insuranceEndDate") String insuranceEndDate) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Insurance file is required"));
        }

        if (insuranceEndDate == null || insuranceEndDate.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Insurance end date is required"));
        }

        try {
            LocalDate endDate = LocalDate.parse(insuranceEndDate);
            carService.uploadInsurance(id, file, endDate);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Insurance uploaded successfully"));
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invalid date format. Please use yyyy-MM-dd format"));
        }
    }

    @GetMapping("/download-insurance/{id}")
    public ResponseEntity<?> downloadInsurance(@PathVariable Integer id) {
        byte[] insuranceData = carService.downloadInsurance(id);
        String filename = String.format("car-%d-insurance.pdf", id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(insuranceData);
    }

    @DeleteMapping("/delete-insurance/{id}")
    public ResponseEntity<?> deleteInsurance(@PathVariable Integer id) {
        carService.deleteInsurance(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Insurance deleted successfully"));
    }

    // Yearly maintenance cost (last 12 months)
    @GetMapping("/maintenance-cost/{make}/{model}/yearly")
    public ResponseEntity<?>  getYearlyMaintenanceCost(
            @PathVariable String make,
            @PathVariable String model,
            @RequestParam(required = false) Integer minMileage,
            @RequestParam(required = false) Integer maxMileage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getMaintenanceCostOneYear(make, model, minMileage, maxMileage));
    }

    // Visit frequency (last 12 months), phrased as “once every ~X years”
    @GetMapping("/visit-frequency/{make}/{model}")
    public ResponseEntity<?>  getVisitFrequency(
            @PathVariable String make,
            @PathVariable String model,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getVisitFrequency(make, model, minAge, maxAge));
    }

    //Get the typical mileage per year for a given car make/model.
    @GetMapping("/typical-mileage/{make}/{model}")
    public ResponseEntity<?>  getTypicalMileagePerYear(
            @PathVariable String make,
            @PathVariable String model,
            @RequestParam(required = false) String city
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getTypicalMileagePerYear(make, model, city));
    }

    @PutMapping("/user/{userId}/car/{carId}/mileage")
    public ResponseEntity<?> updateMileage(@PathVariable Integer userId, @PathVariable Integer carId, @Valid @RequestBody CarMileageUpdateDTO dto) {
        carService.updateMileage(userId, carId, dto.getMileage());
        return ResponseEntity.ok(new ApiResponse("Mileage updated successfully"));
    }

    @GetMapping("/numbers/{userId}")
    public ResponseEntity<?> getCarsNo(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getCarsNumbers(userId));
    }


}
