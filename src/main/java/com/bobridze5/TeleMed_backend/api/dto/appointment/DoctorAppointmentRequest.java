package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DoctorAppointmentRequest(
        @NotNull(message = "Не указан пациент")
        Long targetId,

        @NotNull(message = "Дата и время обязательны")
        @Future(message = "Запись возможна только на будущее время")
        LocalDateTime dateTime,

        @NotNull(message = "Выберите тип консультации")
        ConsultationType consultationType,

        @NotNull(message = "Длительность слота обязательна")
        @Min(value = 5, message = "Длительность слота не может быть меньше 5 минут")
        @Max(value = 240, message = "Длительность слота не может превышать 240 минут")
        Integer slotDurationMinutes
) implements AppointmentCreationRequest {
}
