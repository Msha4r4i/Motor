package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.ReminderDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.Maintenance;
import com.fkhrayef.motor.Model.Reminder;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.ReminderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final CarRepository carRepository;


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

}
