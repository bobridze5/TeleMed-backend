package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.notification.Reminder;
import com.bobridze5.TeleMed_backend.core.entity.notification.ReminderKind;
import com.bobridze5.TeleMed_backend.core.repository.ReminderRepository;
import com.bobridze5.TeleMed_backend.core.service.utils.TimeZoneSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderScheduler {
    private static final String NOTIFICATION_TYPE = "REMINDER";

    /**
     * Окно срабатывания recurring-напоминания после расчётного времени, в минутах.
     * Защищает от пропуска при опоздании планировщика (рестарт сервера, GC,
     * длинная транзакция). Дубли исключены проверкой lastFiredAt по дню в TZ
     * пациента: одно recurring может сработать максимум раз в сутки.
     */
    private static final long RECURRING_WINDOW_MINUTES = 5;

    private final ReminderRepository reminderRepository;
    private final UserNotificationService userNotificationService;
    private final EmailSender emailSender;

    @Scheduled(cron = "0 * * * * *")
    public void tick() {
        log.debug("ReminderScheduler tick");
        fireOneShots();
        fireRecurring();
    }

    @Transactional
    public void fireOneShots() {
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

    @Transactional
    public void fireRecurring() {
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
        // Окно: [target, target + RECURRING_WINDOW_MINUTES]. Раньше времени —
        // ждём; позже — пропускаем (один тик в минуту, дубль одного дня
        // отрезается lastFiredAt-проверкой ниже).
        long deltaMinutes = Duration.between(target, current).toMinutes();
        if (deltaMinutes < 0 || deltaMinutes > RECURRING_WINDOW_MINUTES) return false;

        Instant lastFired = r.getLastFiredAt();
        if (lastFired != null) {
            LocalDate lastFiredDate = lastFired.atZone(tz).toLocalDate();
            if (lastFiredDate.equals(nowInTz.toLocalDate())) return false;
        }
        return true;
    }

    private Set<DayOfWeek> parseDays(String days) {
        if (days == null || days.isBlank()) return Set.of();
        return Arrays.stream(days.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try { return DayOfWeek.valueOf(s.toUpperCase()); }
                    catch (IllegalArgumentException e) { return null; }
                })
                .filter(d -> d != null)
                .collect(Collectors.toSet());
    }

    private void fire(Reminder r) {
        Patient p = r.getPatient();
        userNotificationService.create(p, r, NOTIFICATION_TYPE, r.getTitle(), r.getMessage());
        try {
            String body = r.getMessage() != null ? r.getMessage() : r.getTitle();
            emailSender.send(p.getEmail(), r.getTitle(), body);
        } catch (Exception e) {
            log.warn("Не удалось отправить email для reminder id={}: {}", r.getId(), e.getMessage());
        }
    }
}
