package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.UserDTO;
import com.fkhrayef.motor.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserDTO userDTO ) {
        userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO ) {
        userService.updateUser(id, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("User updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/upload-license/{id}")
    public ResponseEntity<?> uploadLicense(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("licenseExpiry") String licenseExpiry) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("License file is required"));
        }

        if (licenseExpiry == null || licenseExpiry.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("License expiry date is required"));
        }

        try {
            // Parse the expiry date
            LocalDate expiryDate = LocalDate.parse(licenseExpiry);
            
            userService.uploadLicense(id, file, expiryDate);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("License uploaded successfully"));
            
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invalid date format. Please use yyyy-MM-dd format"));
        }
    }

    @GetMapping("/download-license/{id}")
    public ResponseEntity<?> downloadLicense(@PathVariable Integer id) {
        byte[] licenseData = userService.downloadLicense(id);
        
        // Generate filename for download
        String filename = String.format("user-%d-license.pdf", id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(licenseData);
    }

    @DeleteMapping("/delete-license/{id}")
    public ResponseEntity<?> deleteLicense(@PathVariable Integer id) {
        userService.deleteLicense(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("License deleted successfully"));
    }

    @GetMapping("/{userId}/subscription")
    public ResponseEntity<Map<String, String>> getSubscription(@PathVariable Integer userId) {
        String type = userService.getUserSubscriptionType(userId);

        Map<String, String> body = new HashMap<>();
        body.put("subscriptionType", type.isEmpty() ? "FREE" : type);

        return ResponseEntity.ok(body);
    }
}
