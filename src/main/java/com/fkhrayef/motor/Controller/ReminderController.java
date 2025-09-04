package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.Api.ApiResponse;
import com.fkhrayef.motor.DTOin.ReminderDTO;
import com.fkhrayef.motor.Model.Reminder;
import com.fkhrayef.motor.Service.ReminderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reminder")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<Reminder> list = reminderService.getAllReminder();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        Reminder r = reminderService.getReminderById(id);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<?> addReminder(@PathVariable Integer id, @Valid @RequestBody ReminderDTO dto) {
        reminderService.addReminder(id, dto);
        return ResponseEntity.ok(new ApiResponse("Reminder added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable Integer id, @Valid @RequestBody ReminderDTO dto) {
        reminderService.updateReminder(id, dto);
        return ResponseEntity.ok(new ApiResponse("Reminder updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.ok(new ApiResponse("Reminder deleted"));
    }

}
