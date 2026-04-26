package com.bobridze5.TeleMed_backend.api.dto.medcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequest(
        @NotBlank(message = "Заголовок записи обязателен")
        @Size(max = 255)
        String title,

        @Size(max = 5000)
        String complaints,

        @Size(max = 5000)
        String diagnosis,

        @Size(max = 5000)
        String recommendations,

        @Size(max = 5000)
        String prescriptions,

        Long appointmentId
) {
}
