package com.bobridze5.TeleMed_backend.api.dto.profile;

import com.bobridze5.TeleMed_backend.core.entity.medical.DiabetesType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientProfileRequest(
        @NotBlank(message = "Имя обязательно")
        @Size(max = 120)
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 120)
        String lastName,

        @Size(max = 120)
        String middleName,

        @NotNull(message = "Дата рождения обязательна")
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate dateOfBirth,

        @NotNull(message = "Укажите пол")
        @Pattern(regexp = "[MF]", message = "Допустимые значения: M, F")
        String gender,

        @NotNull(message = "Укажите тип диабета")
        DiabetesType diabetesType,

        @PastOrPresent(message = "Дата диагноза не может быть в будущем")
        LocalDate diagnosisDate,

        @NotNull(message = "Укажите зависимость от инсулина")
        Boolean isInsulinDependency,

        @Positive(message = "Нижний порог должен быть больше 0")
        Double targetLow,

        @Positive(message = "Верхний порог должен быть больше 0")
        Double targetHigh
) {
    public PatientProfileRequest {
        if (targetLow != null && targetHigh != null && targetLow >= targetHigh) {
            throw new IllegalArgumentException("Нижний порог должен быть меньше верхнего");
        }
    }

}