package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.notification.Reminder;
import com.bobridze5.TeleMed_backend.core.entity.notification.ReminderKind;
import com.bobridze5.TeleMed_backend.core.repository.ReminderRepository;
import com.bobridze5.TeleMed_backend.core.service.utils.TimeZoneSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderProcessorImpl implements ReminderProcessor {
    private static final String NOTIFICATION_TYPE = "REMINDER";
    private static final long RECURRING_WINDOW_MINUTES = 5;

    private final ReminderRepository reminderRepository;
    private final UserNotificationService userNotificationService;
    private final EmailSender emailSender;

    @Override
    @Transactional
    public void processOneShots() {
        Instant now = Instant.now();
        List<Reminder> due = reminderRepository
                .findAllByEnabledTrueAndKindAndScheduledAtBefore(ReminderKind.ONE_SHOT, now.plusSeconds(1));
        for (Reminder r : due) {
            if (r.getLastFiredAt() != null) continue;
            try {
                fire(r);
                r.setLastFiredAt(Instant.now());
                r.setEnabled(false);
            } catch (Exception e) {
                log.error("Ошибка при отправке one-shot reminder id={}", r.getId(), e);
            }
        }
    }

    @Override
    @Transactional
    public void processRecurring() {
        List<Reminder> all = reminderRepository.findAllByEnabledTrueAndKind(ReminderKind.RECURRING);
        for (Reminder r : all) {
            try {
                if (shouldFireNow(r)) {
                    fire(r);
                    r.setLastFiredAt(Instant.now());
                }
            } catch (Exception e) {
                log.error("Ошибка при отправке recurring reminder id={}", r.getId(), e);
            }
        }
    }


    private boolean shouldFireNow(Reminder r) {
        ZoneId tz = TimeZoneSupport.zoneIdFor(r.getPatient());
        LocalDateTime nowInTz = LocalDateTime.now(tz);
        DayOfWeek today = nowInTz.getDayOfWeek();

        Set<DayOfWeek> days = parseDays(r.getRecurrenceDays());
        if (!days.contains(today)) return false;

        LocalTime target = r.getRecurrenceTime();
        LocalTime current = nowInTz.toLocalTime();

        long deltaMinutes = Duration.between(target, current).toMinutes();
        if (deltaMinutes < 0 || deltaMinutes > RECURRING_WINDOW_MINUTES) return false;

        Instant lastFired = r.getLastFiredAt();
        if (lastFired != null) {
            LocalDate lastFiredDate = lastFired.atZone(tz).toLocalDate();
            return !lastFiredDate.equals(nowInTz.toLocalDate());
        }
        return true;
    }

    private Set<DayOfWeek> parseDays(String days) {
        if (days == null || days.isBlank()) return Set.of();

        return Arrays.stream(days.split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return DayOfWeek.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void fire(Reminder reminder) {
        Patient patient = reminder.getPatient();
        userNotificationService.create(patient, reminder, NOTIFICATION_TYPE, reminder.getTitle(), reminder.getMessage());

        try {
            String body = reminder.getMessage() != null ? reminder.getMessage() : reminder.getTitle();
            emailSender.send(patient.getEmail(), reminder.getTitle(), body);
        } catch (Exception e) {
            log.warn("Не удалось отправить email для reminder id={}: {}", reminder.getId(), e.getMessage());
        }
    }
}
