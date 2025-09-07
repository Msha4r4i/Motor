package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.ReminderDTO;
import com.fkhrayef.motor.DTOout.MaintenanceReminderResponseDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.Reminder;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.ReminderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final CarRepository carRepository;
    private final RAGService ragService;
    private final WhatsAppService whatsappService;
    private final EmailService emailService;
    



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

    @Transactional
    public void generateAndSaveMaintenanceReminders(Integer carId) {

        // Find the car
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found");
        }

        // Mileage is required by RAG and cannot be null (Map.of forbids nulls)
        if (car.getMileage() == null) {
            throw new ApiException("Car mileage is required to generate maintenance reminders.");
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
        if (ragResponse.getReminders() == null || ragResponse.getReminders().isEmpty()) {
            throw new ApiException("RAG returned no maintenance reminders for this car/manual.");
        }

        // Convert RAG response to Reminder entities and save in batch
        List<Reminder> toSave = ragResponse.getReminders().stream()
                .map(reminderData -> {
                    Reminder reminder = new Reminder();
                    reminder.setType("maintenance");
                    reminder.setDueDate(LocalDate.parse(reminderData.getDueDate().trim()));
                    reminder.setMessage(reminderData.getMessage());
                    reminder.setMileage(reminderData.getMileage());
                    reminder.setPriority(reminderData.getPriority());
                    reminder.setCategory(reminderData.getCategory());
                    reminder.setCar(car);
                    reminder.setIsSent(false);
                    return reminder;
                })
                .filter(reminder -> {
                    // Check if reminder already exists (same car, type, dueDate, message)
                    return !reminderRepository.existsByCarAndTypeAndDueDateAndMessage(
                            car, "maintenance", reminder.getDueDate(), reminder.getMessage());
                })
                .collect(Collectors.toList());

        reminderRepository.saveAll(toSave);
    }

    // Helper method to generate document name from car details (same as CarAIService)
    private String generateDocumentName(Car car) {
        return String.format("%d %s %s owner-manual",
                car.getYear(),
                car.getMake(),
                car.getModel());
    }

    /**
     * Scheduled task to send reminder notifications
     * Runs daily at 9:00 AM to check for upcoming reminders
     */
    @Scheduled(cron = "0 * * * * *") // Every minute (for testing)
    public void sendReminderNotifications() {
        try {
            log.info("[Scheduler] Starting reminder notification check...");
            
            LocalDate today = LocalDate.now();
            LocalDate nextWeek = today.plusDays(7);
            LocalDate tomorrow = today.plusDays(1);
            
            // Fetch once
            List<Reminder> allReminders = reminderRepository.findAll();

            // Daily: due tomorrow (send regardless of isSent)
            List<Reminder> tomorrowReminders = allReminders.stream()
                    .filter(r -> r.getDueDate() != null)
                    .filter(r -> r.getDueDate().isEqual(tomorrow))
                    .collect(Collectors.toList());

            // Weekly: due in 2..7 days (exclude 'tomorrow' to avoid duplicate sends)
            List<Reminder> upcomingReminders = allReminders.stream()
                    .filter(r -> Boolean.FALSE.equals(r.getIsSent()))
                    .filter(r -> r.getDueDate() != null)
                    .filter(r -> r.getDueDate().isAfter(tomorrow))      // > tomorrow
                    .filter(r -> !r.getDueDate().isAfter(nextWeek))     // <= nextWeek
                    .collect(Collectors.toList());
            
            // Send notifications for reminders due in next week
            for (Reminder reminder : upcomingReminders) {
                try {
                    sendReminderNotification(reminder, "week");
                } catch (Exception e) {
                    log.error("[Scheduler] Failed to send weekly reminder notification for reminder ID {}: {}", 
                            reminder.getId(), e.getMessage());
                }
            }
            
            // Send notifications for reminders due tomorrow
            for (Reminder reminder : tomorrowReminders) {
                try {
                    sendReminderNotification(reminder, "day");
                } catch (Exception e) {
                    log.error("[Scheduler] Failed to send daily reminder notification for reminder ID {}: {}", 
                            reminder.getId(), e.getMessage());
                }
            }
            
            log.info("[Scheduler] Reminder notification check completed. Sent {} weekly and {} daily notifications.", 
                    upcomingReminders.size(), tomorrowReminders.size());
                    
        } catch (Exception e) {
            log.error("[Scheduler] Reminder notification job failed: {}", e.getMessage());
        }
    }
    
    /**
     * Send notification for a specific reminder
     */
    private void sendReminderNotification(Reminder reminder, String notificationType) {
        Car car = reminder.getCar();
        if (car == null) return;
        
        User user = car.getUser();
        if (user == null) return;
        
        String message = buildReminderMessage(reminder, car, notificationType);

        if (notificationType.equals("day")) {
            // Send WhatsApp notification
            try {
                if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                    whatsappService.sendWhatsAppMessage(message, user.getPhone());
                    log.info("[Scheduler] WhatsApp notification sent to user {} for reminder ID {}",
                            user.getId(), reminder.getId());
                }
            } catch (Exception e) {
                log.error("[Scheduler] Failed to send WhatsApp notification: {}", e.getMessage());
            }
        } else {

            // Send Email notification
            try {
                if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                    String subject = "تذكير صيانة - " + car.getMake() + " " + car.getModel();
                    emailService.sendEmail(user.getEmail(), subject, message);
                    log.info("[Scheduler] Email notification sent to user {} for reminder ID {}",
                            user.getId(), reminder.getId());
                }
            } catch (Exception e) {
                log.error("[Scheduler] Failed to send email notification: {}", e.getMessage());
            }
        }
        
        // Mark reminder as sent (only for weekly notifications)
        if (notificationType.equals("week")) {
            reminder.setIsSent(true);
            reminderRepository.save(reminder);
        }
    }
    
    /**
     * Build reminder message in Arabic
     */
    private String buildReminderMessage(Reminder reminder, Car car, String notificationType) {
        String timeFrame = notificationType.equals("day") ? "غداً" : "خلال الأسبوع القادم";
        
        StringBuilder message = new StringBuilder();
        message.append("🔔 تذكير صيانة\n\n");
        message.append("📋 تفاصيل السيارة:\n");
        message.append("• الماركة: ").append(car.getMake()).append("\n");
        message.append("• الموديل: ").append(car.getModel()).append("\n");
        message.append("• السنة: ").append(car.getYear()).append("\n\n");
        
        message.append("⚠️ التذكير:\n");
        message.append("• النوع: ").append(getReminderTypeInArabic(reminder.getType())).append("\n");
        message.append("• التاريخ: ").append(reminder.getDueDate()).append(" (").append(timeFrame).append(")\n");
        message.append("• الرسالة: ").append(reminder.getMessage()).append("\n");
        
        if (reminder.getMileage() != null) {
            message.append("• الكيلومترات المستهدفة: ").append(reminder.getMileage()).append("\n");
        }
        
        if (reminder.getPriority() != null) {
            message.append("• الأولوية: ").append(getPriorityInArabic(reminder.getPriority())).append("\n");
        }
        
        if (reminder.getCategory() != null) {
            message.append("• الفئة: ").append(reminder.getCategory()).append("\n");
        }
        
        message.append("\nيرجى مراجعة جدول الصيانة والاستعداد للصيانة المطلوبة.");
        
        return message.toString();
    }
    
    /**
     * Get reminder type in Arabic
     */
    private String getReminderTypeInArabic(String type) {
        if (type == null) return "غير معروف";
        switch (type) {
            case "maintenance":
                return "صيانة";
            case "license_expiry":
                return "انتهاء الرخصة";
            case "insurance_expiry":
                return "انتهاء التأمين";
            case "registration_expiry":
                return "انتهاء التسجيل";
            default:
                return type;
        }
    }
    
    /**
     * Get priority in Arabic
     */
    private String getPriorityInArabic(String priority) {
        if (priority == null) return "غير معروف";
        switch (priority.toLowerCase()) {
            case "high":
                return "عالي";
            case "medium":
                return "متوسط";
            case "low":
                return "منخفض";
            default:
                return priority;
        }
    }

    /**
     * Scheduled job: every Monday 9:00 AM send WhatsApp reminder
     * to update car mileage
     */
    @Scheduled(cron = "0 0 9 * * MON") // كل يوم اثنين الساعة 9 صباحاً
    public void sendWeeklyMileageReminders() {
        log.info("[Scheduler] Starting weekly mileage reminders...");

        List<Car> cars = carRepository.findAll();
        for (Car car : cars) {
            if (car.getUser() == null) {
                continue;
            }

            User user = car.getUser();
            if (user.getPhone() == null || user.getPhone().isBlank()){
                continue;
            }

            String message = buildMileageReminderMessage(car);

            try {
                whatsappService.sendWhatsAppMessage(message, user.getPhone());
                log.info("[Scheduler] Weekly mileage reminder sent to user {} for car {}",
                        user.getId(), car.getId());
            } catch (Exception e) {
                log.error("[Scheduler] Failed to send weekly mileage reminder to user {}: {}",
                        user.getId(), e.getMessage());
            }
        }
    }

    private String buildMileageReminderMessage(Car car) {
        StringBuilder msg = new StringBuilder();
        msg.append("🚗 تذكير أسبوعي لتحديث عداد السيارة\n\n");
        msg.append("📋 تفاصيل السيارة:\n");
        msg.append("• الماركة: ").append(car.getMake()).append("\n");
        msg.append("• الموديل: ").append(car.getModel()).append("\n");
        msg.append("• السنة: ").append(car.getYear()).append("\n\n");
        msg.append("🔢 العداد الحالي المسجل: ").append(car.getMileage() != null ? car.getMileage() : "غير مسجل").append("\n\n");
        msg.append("💡 يرجى إدخال القراءة الجديدة للعداد عبر التطبيق للحفاظ على سجل الصيانة محدثاً.");
        return msg.toString();
    }

}
