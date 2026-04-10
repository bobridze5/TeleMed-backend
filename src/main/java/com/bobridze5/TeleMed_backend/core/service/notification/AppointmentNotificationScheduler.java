package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentNotificationScheduler {
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 3_600_000) // Запускается каждый час (3600000 мс)
    public void sendReminders() {
        log.info("Запуск планировщика напоминаний о приёмах");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);

        // Ищем приёмы, которые начинаются между текущим временем + 23.5 часа и текущим временем + 24.5 часа
        LocalDateTime searchStart = now.plusHours(23).minusMinutes(30);
        LocalDateTime searchEnd = now.plusHours(24).plusMinutes(30);

        List<Appointment> upcomingAppointments = appointmentRepository.findAppointmentsInTimeRangeForNotifications(
                searchStart, searchEnd,
                Set.of(AppointmentStatus.CREATED, AppointmentStatus.CONFIRMED)
        );

        if (upcomingAppointments.isEmpty()) {
            log.info("Нет приёмов для отправки напоминаний в интервале {} - {}", searchStart, searchEnd);
            return;
        }

        log.info("Найдено {} приёмов для отправки напоминаний", upcomingAppointments.size());

        for (Appointment appointment : upcomingAppointments) {
            try {
                notificationService.sendConfirmationRequest(appointment);
            } catch (Exception e) {
                log.error("Ошибка при отправке напоминания для приёма ID: {}", appointment.getId(), e);
            }
        }

        log.info("Планировщик напоминаний завершил работу");
    }
}
