package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record AppointmentUpdateRequest(
        @Future(message = "Запись возможна только на будущее время")
        LocalDateTime dateTime,
        ConsultationType consultationType,
        String meetingLink,
        String meetingPhone,
        String meetingNotes,
        String reason
) {
}
