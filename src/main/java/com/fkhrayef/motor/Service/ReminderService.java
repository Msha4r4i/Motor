package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.ReminderDTO;
import com.fkhrayef.motor.Model.Car;
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


    public List<Reminder> getAllReminder(){
        return reminderRepository.findAll();
    }

    public Reminder getReminderById(Integer id) {
        Reminder r = reminderRepository.findReminderById(id);
        if (r == null) throw new ApiException("Reminder not found");
        return r;
    }

    public void addReminder(Integer carId, ReminderDTO reminderDTO) {
        Car car = carRepository.findCarById(carId);
        if (car == null) throw new ApiException("Car not found");

        Reminder reminder = new Reminder();

        reminder.setType(reminderDTO.getType());
        reminder.setDueDate(reminderDTO.getDueDate());
        reminder.setMessage(reminderDTO.getMessage());
        reminder.setIsSent(false);
        reminder.setCar(car);

        reminderRepository.save(reminder);
    }

    public void updateReminder(Integer id, ReminderDTO dto) {
        Reminder reminder = reminderRepository.findReminderById(id);
        if (reminder == null) throw new ApiException("Reminder not found");

        reminder.setType(dto.getType());
        reminder.setDueDate(dto.getDueDate());
        reminder.setMessage(dto.getMessage());
        reminderRepository.save(reminder);
    }

    public void deleteReminder(Integer id) {
        Reminder reminder = reminderRepository.findReminderById(id);
        if (reminder == null) throw new ApiException("Reminder not found");
        reminderRepository.delete(reminder);
    }

}
