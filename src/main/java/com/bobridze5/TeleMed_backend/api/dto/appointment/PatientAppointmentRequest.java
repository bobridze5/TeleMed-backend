package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PatientAppointmentRequest(
        @NotNull(message = "Не указан врач")
        Long targetId,

        @NotNull(message = "Дата и время обязательны")
        @Future(message = "Запись возможна только на будущее время")
        LocalDateTime dateTime,

        @NotNull(message = "Выберите тип консультации")
        ConsultationType consultationType
) implements AppointmentCreationRequest {
}
