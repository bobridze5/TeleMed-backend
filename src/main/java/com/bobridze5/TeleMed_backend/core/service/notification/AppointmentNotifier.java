package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Точка входа для всех in-app уведомлений, связанных с приёмами. Создаёт
 * записи в unified-таблице notification (для иконки колокольчика в шапке) —
 * отдельно для пациента и для врача. Email-канал реализован параллельно в
 * {@link AppointmentEmailService}, и оба канала склеиваются в
 * {@link AppointmentNotificationScheduler}.
 *
 * Все методы намеренно не транзакционные: они вызываются из методов, которые
 * сами @Transactional, и должны разделять транзакцию с операцией над приёмом.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentNotifier {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final String TYPE_NEW = "APPOINTMENT_NEW";
    private static final String TYPE_CONFIRMED = "APPOINTMENT_CONFIRMED";
    private static final String TYPE_CANCELED = "APPOINTMENT_CANCELED";
    private static final String TYPE_RESCHEDULED = "APPOINTMENT_RESCHEDULED";
    private static final String TYPE_REMINDER = "APPOINTMENT_REMINDER";

    private final UserNotificationService userNotificationService;

    /**
     * Создаётся новая запись на приём.
     *  — Если запись инициировал пациент, врач получает «новая запись»,
     *    пациент — подтверждение «вы записаны».
     *  — Если запись инициировал врач, пациент получает уведомление о записи,
     *    врач — подтверждение.
     */
    public void onCreated(Appointment appointment, boolean initiatedByDoctor) {
        try {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();
            String when = appointment.getDateTime().format(FMT);
            String typeRu = consultationLabel(appointment.getConsultationType());

            String patientTitle = "Запись к врачу создана";
            String patientMsg = String.format(
                    "Запись к врачу %s на %s (%s) %s",
                    fullName(doctor),
                    when, typeRu,
                    initiatedByDoctor ? "оформлена врачом." : "ожидает подтверждения врача."
            );
            userNotificationService.create(patient, null, TYPE_NEW, patientTitle, patientMsg);

            String doctorTitle = "Новая запись на приём";
            String doctorMsg = String.format(
                    "Пациент %s записан на %s (%s)%s",
                    fullName(patient), when, typeRu,
                    initiatedByDoctor ? "." : ". Подтвердите запись."
            );
            userNotificationService.create(doctor, null, TYPE_NEW, doctorTitle, doctorMsg);
        } catch (Exception e) {
            log.error("Не удалось создать notification для записи ID={}", appointment.getId(), e);
        }
    }

    /**
     * Кто-то одной из сторон подтвердил запись. notifyRole = "PATIENT" или
     * "DOCTOR" — это та сторона, которая ТОЛЬКО ЧТО подтвердила; другая
     * получит уведомление об этом.
     */
    public void onConfirmed(Appointment appointment, String confirmedRole) {
        try {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();
            String when = appointment.getDateTime().format(FMT);
            boolean fullyConfirmed = Boolean.TRUE.equals(appointment.getConfirmedByPatient())
                    && Boolean.TRUE.equals(appointment.getConfirmedByDoctor());

            String title = fullyConfirmed ? "Приём подтверждён" : "Запись подтверждена другой стороной";

            if ("PATIENT".equals(confirmedRole)) {
                userNotificationService.create(doctor, null, TYPE_CONFIRMED, title,
                        String.format("Пациент %s подтвердил запись на %s.%s",
                                fullName(patient), when,
                                fullyConfirmed ? " Приём состоится." : " Ожидаем ваше подтверждение."));
            } else if ("DOCTOR".equals(confirmedRole)) {
                userNotificationService.create(patient, null, TYPE_CONFIRMED, title,
                        String.format("Врач %s подтвердил запись на %s.%s",
                                fullName(doctor), when,
                                fullyConfirmed ? " Приём состоится." : " Подтвердите со своей стороны."));
            }
        } catch (Exception e) {
            log.error("Не удалось создать notification (confirm) для записи ID={}", appointment.getId(), e);
        }
    }

    /**
     * Запись отменена. cancelledBy = роль того, кто отменил.
     * Уведомление получает противоположная сторона.
     */
    public void onCanceled(Appointment appointment, String cancelledBy, String reason) {
        try {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();
            String when = appointment.getDateTime().format(FMT);
            String reasonStr = (reason != null && !reason.isBlank())
                    ? " Причина: " + reason : "";
            String title = "Запись отменена";

            if ("PATIENT".equals(cancelledBy)) {
                userNotificationService.create(doctor, null, TYPE_CANCELED, title,
                        String.format("Пациент %s отменил запись на %s.%s",
                                fullName(patient), when, reasonStr));
            } else {
                userNotificationService.create(patient, null, TYPE_CANCELED, title,
                        String.format("Врач %s отменил запись на %s.%s",
                                fullName(doctor), when, reasonStr));
            }
        } catch (Exception e) {
            log.error("Не удалось создать notification (cancel) для записи ID={}", appointment.getId(), e);
        }
    }

    /**
     * Запись перенесена врачом (изменено время и/или тип консультации).
     * Уведомление получает только пациент: бизнес-правило допускает перенос
     * исключительно из CREATED при отсутствии подтверждений с обеих сторон,
     * поэтому врач сам и инициатор, и единственный, кто видел старое время.
     */
    public void onRescheduled(Appointment appointment,
                              LocalDateTime previousDateTime,
                              ConsultationType previousType) {
        try {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();
            String oldWhen = previousDateTime != null ? previousDateTime.format(FMT) : "—";
            String newWhen = appointment.getDateTime().format(FMT);
            String newType = consultationLabel(appointment.getConsultationType());

            boolean dateChanged = previousDateTime != null
                    && !previousDateTime.equals(appointment.getDateTime());
            boolean typeChanged = previousType != null
                    && previousType != appointment.getConsultationType();

            StringBuilder msg = new StringBuilder();
            msg.append("Врач ").append(fullName(doctor)).append(" перенёс приём");
            if (dateChanged) {
                msg.append(" с ").append(oldWhen).append(" на ").append(newWhen);
            } else {
                msg.append(" на ").append(newWhen);
            }
            if (typeChanged) {
                msg.append(" (").append(newType).append(")");
            }
            msg.append(". Подтвердите запись.");

            userNotificationService.create(patient, null, TYPE_RESCHEDULED,
                    "Запись перенесена", msg.toString());
        } catch (Exception e) {
            log.error("Не удалось создать notification (reschedule) для записи ID={}",
                    appointment.getId(), e);
        }
    }

    /**
     * Напоминание за день — для обеих сторон. Используется в
     * {@link AppointmentNotificationScheduler}.
     */
    public void onReminder(Appointment appointment) {
        try {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();
            String when = appointment.getDateTime().format(FMT);
            String typeRu = consultationLabel(appointment.getConsultationType());

            userNotificationService.create(patient, null, TYPE_REMINDER,
                    "Напоминание о приёме",
                    String.format("Завтра, %s — приём у врача %s (%s).",
                            when, fullName(doctor), typeRu));

            userNotificationService.create(doctor, null, TYPE_REMINDER,
                    "Напоминание о приёме",
                    String.format("Завтра, %s — приём пациента %s (%s).",
                            when, fullName(patient), typeRu));
        } catch (Exception e) {
            log.error("Не удалось создать reminder notification для записи ID={}", appointment.getId(), e);
        }
    }

    private static String fullName(com.bobridze5.TeleMed_backend.core.entity.auth.User user) {
        if (user == null) return "—";
        String full = user.getFullName();
        return (full == null || full.isBlank()) ? user.getEmail() : full;
    }

    private static String consultationLabel(ConsultationType type) {
        if (type == null) return "консультация";
        return switch (type) {
            case VIDEO -> "видео";
            case AUDIO -> "аудио";
            case CHAT -> "чат";
        };
    }
}
