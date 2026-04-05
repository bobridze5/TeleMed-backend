package com.bobridze5.TeleMed_backend.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorRejectRequest(
        @NotBlank(message = "Укажите причину отклонения")
        @Size(max = 500, message = "Причина не может превышать 500 символов")
        String reason
) {
}
