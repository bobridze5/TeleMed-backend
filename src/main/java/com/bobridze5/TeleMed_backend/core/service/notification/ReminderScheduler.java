package com.bobridze5.TeleMed_backend.core.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderScheduler {
    private final ReminderProcessor reminderProcessor;

    @Scheduled(cron = "0 * * * * *")
    public void tick() {
        log.debug("ReminderScheduler tick");
        reminderProcessor.processOneShots();
        reminderProcessor.processRecurring();
    }
}
