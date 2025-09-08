package com.fkhrayef.motor.Service;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirySchedulerService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final EmailService emailService;

    // كل يوم 9 صباحًا
    @Scheduled(cron = "0 0 9 * * *")
    public void sendLicenseExpiryAlerts() {
        LocalDate target = LocalDate.now().plusMonths(1); // بعد شهر
        List<User> users = userRepository.findByLicenseExpiry(target);

        for (User u : users) {
            try {
                if (u.getEmail() == null || u.getEmail().isBlank()){
                    continue;
                }
                if (u.getLicenseExpiry() == null){
                    continue;
                }

                String subject = "تنبيه: انتهاء رخصتك بعد شهر";
                String body =
                        "مرحبًا " + u.getName() + "،\n\n" +
                                "نودّ تنبيهك بأن تاريخ انتهاء رخصتك سيكون في: " + u.getLicenseExpiry() + ".\n" +
                                "يرجى التجديد قبل التاريخ المحدد لتجنب الغرامات.\n\n" +
                                "تحياتنا، فريق Motor";

                emailService.sendEmail(u.getEmail(), subject, body);

            } catch (Exception e) {
                log.warn("Failed to send license expiry email: userId={}, email={}",
                        u.getId(), u.getEmail(), e);
            }
        }
    }

    // كل يوم 9:10 صباحًا
    @Scheduled(cron = "0 10 9 * * *")
    public void sendCarExpiryAlerts() {
        LocalDate target = LocalDate.now().plusMonths(1); // بعد شهر

        // الاستمارة
        List<Car> regCars = carRepository.findByRegistrationExpiry(target);
        for (Car c : regCars) {
            try {
                if (c.getRegistrationExpiry() == null){
                    continue;
                }
                if (c.getUser() == null){
                    continue;
                }
                User u = c.getUser();
                if (u.getEmail() == null || u.getEmail().isBlank()){
                    continue;
                }

                String subject = "تنبيه: انتهاء استمارة سيارتك بعد شهر";
                String body =
                        "مرحبًا " + u.getName() + "،\n\n" +
                                "سيارتك: " + c.getMake() + " " + c.getModel() + " (" + c.getYear() + ")\n" +
                                "تاريخ انتهاء الاستمارة: " + c.getRegistrationExpiry() + ".\n" +
                                "يرجى التجديد قبل التاريخ المحدد.\n\n" +
                                "تحياتنا، فريق Motor";

                emailService.sendEmail(u.getEmail(), subject, body);

            } catch (Exception e) {
                log.warn("Failed to send registration expiry email: carId={}, userId={}, email={}",
                        c.getId(), c.getUser()!=null?c.getUser().getId():null,
                        c.getUser()!=null?c.getUser().getEmail():null, e);
            }
        }

        // التأمين
        List<Car> insCars = carRepository.findByInsuranceEndDate(target);
        for (Car c : insCars) {
            try {
                if (c.getInsuranceEndDate() == null){
                    continue;
                }
                if (c.getUser() == null){
                    continue;
                }
                User u = c.getUser();
                if (u.getEmail() == null || u.getEmail().isBlank()){
                    continue;
                }

                String subject = "تنبيه: انتهاء تأمين سيارتك بعد شهر";
                String body =
                        "مرحبًا " + u.getName() + "،\n\n" +
                                "سيارتك: " + c.getMake() + " " + c.getModel() + " (" + c.getYear() + ")\n" +
                                "تاريخ انتهاء التأمين: " + c.getInsuranceEndDate() + ".\n" +
                                "يرجى التجديد قبل التاريخ المحدد.\n\n" +
                                "تحياتنا، فريق Motor";

                emailService.sendEmail(u.getEmail(), subject, body);

            } catch (Exception e) {
                log.warn("Failed to send insurance expiry email: carId={}, userId={}, email={}",
                        c.getId(), c.getUser()!=null?c.getUser().getId():null,
                        c.getUser()!=null?c.getUser().getEmail():null, e);
            }
        }
    }
}