package com.cypr.scheduler;

import com.cypr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class CreditResetScheduler {

    @Autowired
    private UserRepository userRepository;

    // EveryDay At 12 AM (Cron expression: sec min hour day month weekday)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyCredits() {
        System.out.println("Starting Credit Reset Scheduled Task...");
        LocalDate today = LocalDate.now();

        userRepository.findAll().forEach(user -> {
            boolean changed = false;
            if (user.getLastResetDate() == null) {
                user.setLastResetDate(today.minusDays(1));
                changed = true;
            }
            if ("FREE".equalsIgnoreCase(user.getSubscriptionType())) {
                if (user.getLastResetDate().isBefore(today)) {
                    user.setCredits(5);
                    user.setLastResetDate(today);
                    changed = true;
                }
            } else if ("PRO".equalsIgnoreCase(user.getSubscriptionType()) || "PLUS".equalsIgnoreCase(user.getSubscriptionType())) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(user.getLastResetDate(), today);
                if (days >= 7) {
                    user.setCredits(15);
                    user.setLastResetDate(today);
                    changed = true;
                }
            }
            if (changed) {
                userRepository.save(user);
            }
        });

        System.out.println("Scheduled credit reset tasks evaluated and saved successfully!");
    }
}