package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Email-канал доставки напоминаний о приёмах. Формирует тело письма для
 * пациента и врача, делегирует фактическую отправку {@link EmailSender}.
 * Триггерится из {@link AppointmentNotificationScheduler} за ~24 часа до
 * приёма; параллельно создаётся in-app уведомление через
 * {@link AppointmentNotifier#onReminder}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEmailService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final EmailSender emailSender;

    @Value("${mail.appointment.reminder.subject:Напоминание о приёме у врача}")
    private String reminderSubject;

    @Value("${mail.appointment.reminder.body:Напоминаем вам о приёме у врача %s %s}")
    private String reminderBodyTemplate;

    /**
     * Отправляет напоминание о приёме на email пациента и врача (~ за 24ч до приёма).
     */
    public void sendAppointmentReminder(Appointment appointment) {
        try {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            String patientEmail = patient.getEmail();
            String doctorEmail = doctor.getEmail();
            String patientName = patient.getFullName();
            String doctorName = doctor.getFullName();
            String appointmentDateTime = appointment.getDateTime().format(DATE_FORMATTER);

            String patientBody = String.format(
                    "Добрый день, %s!\n\n" +
                            "Напоминаем вам о приёме у врача %s.\n" +
                            "Дата и время: %s\n" +
                            "Тип консультации: %s\n" +
                            "%s\n\n" +
                            "С уважением,\nTeleMed",
                    patientName,
                    doctorName,
                    appointmentDateTime,
                    appointment.getConsultationType(),
                    appointment.getMeetingLink() != null ? "Ссылка на встречу: " + appointment.getMeetingLink() : ""
            );

            String doctorBody = String.format(
                    "Добрый день, %s!\n\n" +
                            "Напоминаем вам о приёме пациента %s.\n" +
                            "Дата и время: %s\n" +
                            "Тип консультации: %s\n" +
                            "Причина визита: %s\n" +
                            "%s\n\n" +
                            "С уважением,\nTeleMed",
                    doctorName,
                    patientName,
                    appointmentDateTime,
                    appointment.getConsultationType(),
                    appointment.getReason() != null ? appointment.getReason() : "Не указана",
                    appointment.getMeetingLink() != null ? "Ссылка на встречу: " + appointment.getMeetingLink() : ""
            );

            emailSender.send(patientEmail, reminderSubject, patientBody);
            emailSender.send(doctorEmail, reminderSubject, doctorBody);

            log.info("Напоминание об приёме отправлено пациенту {} и врачу {} для приёма ID: {}",
                    patientEmail, doctorEmail, appointment.getId());
        } catch (Exception e) {
            log.error("Ошибка при отправке напоминания об приёме ID: {}", appointment.getId(), e);
            throw new RuntimeException("Не удалось отправить напоминание об приёме", e);
        }
    }
}
