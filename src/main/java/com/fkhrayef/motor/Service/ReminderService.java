package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.ReminderDTO;
import com.fkhrayef.motor.DTOout.MaintenanceReminderResponseDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.Reminder;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.ReminderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final CarRepository carRepository;
    private final RAGService ragService;


    public List<Reminder> getAllReminders(){
        return reminderRepository.findAll();
    }

    public void addReminder(Integer carId, ReminderDTO reminderDTO) {
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found");
        }

        Reminder reminder = new Reminder();

        reminder.setType(reminderDTO.getType());
        reminder.setDueDate(reminderDTO.getDueDate());
        reminder.setMessage(reminderDTO.getMessage());
        reminder.setIsSent(false);
        reminder.setCar(car);

        reminderRepository.save(reminder);
    }

    public void updateReminder(Integer id, ReminderDTO reminderDTO) {
        Reminder reminder = reminderRepository.findReminderById(id);
        if (reminder == null) {
            throw new ApiException("Reminder not found");
        }

        reminder.setType(reminderDTO.getType());
        reminder.setDueDate(reminderDTO.getDueDate());
        reminder.setMessage(reminderDTO.getMessage());
        reminderRepository.save(reminder);
    }

    public void deleteReminder(Integer id) {
        Reminder reminder = reminderRepository.findReminderById(id);
        if (reminder == null) {
            throw new ApiException("Reminder not found");
        }
        reminderRepository.delete(reminder);
    }

    public List<Reminder> getRemindersByCarId(Integer carId){
        Car car = carRepository.findCarById(carId);

        if (car == null){
            throw new ApiException("Car not found");
        }
        return reminderRepository.findRemindersByCarId(car.getId());
    }

    public void generateAndSaveMaintenanceReminders(Integer carId) {

        // Find the car
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found");
        }

        // Generate document name from car details (same as /ask endpoint)
        String documentName = generateDocumentName(car);

        // Check if document exists in RAG system
        if (!ragService.documentExists(documentName)) {
            throw new ApiException("Manual for this car is not available. Please upload the manual first.");
        }

        // Call RAG API to get maintenance reminders
        MaintenanceReminderResponseDTO ragResponse = ragService.generateMaintenanceReminders(car.getMileage(), documentName);

        if (ragResponse == null || !ragResponse.getSuccess()) {
            throw new ApiException("Failed to generate maintenance reminders: " + 
                (ragResponse != null ? ragResponse.getError() : "Unknown error"));
        }

        // Convert RAG response to Reminder entities and save
        ragResponse.getReminders().stream()
                .map(reminderData -> {
                    Reminder reminder = new Reminder();
                    reminder.setType("maintenance");
                    reminder.setDueDate(LocalDate.parse(reminderData.getDueDate()));
                    reminder.setMessage(reminderData.getMessage());
                    reminder.setMileage(reminderData.getMileage());
                    reminder.setPriority(reminderData.getPriority());
                    reminder.setCategory(reminderData.getCategory());
                    reminder.setCar(car);
                    reminder.setIsSent(false);

                    return reminderRepository.save(reminder);
                })
                .collect(Collectors.toList());
    }

    // Helper method to generate document name from car details (same as CarAIService)
    private String generateDocumentName(Car car) {
        return String.format("%d %s %s owner-manual",
                car.getYear(),
                car.getMake(),
                car.getModel());
    }

}
