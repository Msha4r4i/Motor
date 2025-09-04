package com.fkhrayef.motor.Repository;

import com.fkhrayef.motor.Model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    Reminder findReminderById(Integer id);
}
