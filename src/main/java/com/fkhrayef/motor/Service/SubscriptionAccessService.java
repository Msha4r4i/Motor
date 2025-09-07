package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionAccessService {

    private final UserRepository userRepository;
    private final CarService carService;

  //  @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Riyadh")
    @Scheduled(cron = "0 * * * * *") // Every minute (for testing)
    public void enforceAllUsersAccessPaged() {
        int page = 0;
        Page<User> slice;
        do {
            slice = userRepository.findAll(PageRequest.of(page, 500));
            log.info("Enforcing access for {} users (page {})", slice.getNumberOfElements(), page);
            slice.forEach(u -> carService.enforceCarAccess(u.getId()));
            page++;
        } while (slice.hasNext());
    }


}
