package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.api.dto.params.DateFilter;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record AppointmentFilterRequest(
        @PositiveOrZero(message = "Номер страницы не может быть отрицательным")
        Integer page,

        @PositiveOrZero(message = "Размер страницы не может быть отрицательным")
        Integer size,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate,

        AppointmentStatus status
) implements DateFilter {
}
