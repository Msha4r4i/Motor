package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.ReminderDTO;
import com.fkhrayef.motor.Service.ReminderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllReminders() {
        return ResponseEntity.status(HttpStatus.OK).body(reminderService.getAllReminders());
    }

    @PostMapping("/add/{carId}")
    public ResponseEntity<?> addReminder(@PathVariable Integer carId, @Valid @RequestBody ReminderDTO reminderDTO) {
        reminderService.addReminder(carId, reminderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Reminder added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable Integer id, @Valid @RequestBody ReminderDTO reminderDTO) {
        reminderService.updateReminder(id, reminderDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Reminder updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/get/{carId}")
    public ResponseEntity<?> getRemindersByCarId(@PathVariable Integer carId) {
        return ResponseEntity.status(HttpStatus.OK).body(reminderService.getRemindersByCarId(carId));
    }

}
