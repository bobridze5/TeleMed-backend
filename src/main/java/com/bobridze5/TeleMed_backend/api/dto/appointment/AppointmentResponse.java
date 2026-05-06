package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        String patientName,
        String doctorName,
        LocalDateTime appointmentDateTime,
        ConsultationType consultationType,
        AppointmentStatus status,
        String meetingLink,
        String meetingPhone,
        String meetingNotes,
        String reason,
        Boolean confirmedByPatient,
        Boolean confirmedByDoctor,
        LocalDateTime timestamp,
        // ID существующей записи в медкарту (если врач уже добавил после
        // приёма) — null, если ещё не создана. Используется фронтом, чтобы
        // подменить кнопку «Добавить запись» на «Редактировать запись».
        Long medicalRecordId,
        // ID отзыва пациента об этом приёме — null если ещё нет.
        // Используется, чтобы пациент мог редактировать/удалять свой отзыв.
        Long reviewId
) {
}
