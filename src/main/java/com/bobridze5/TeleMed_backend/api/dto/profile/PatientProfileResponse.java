package com.bobridze5.TeleMed_backend.api.dto.profile;

import com.bobridze5.TeleMed_backend.core.entity.medical.DiabetesType;

import java.time.LocalDate;

public record PatientProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        LocalDate dateBirth,
        String gender,
        String email,
        DiabetesType diabetesType,
        LocalDate diagnosisDate,
        Boolean isInsulinDependency,
        Double targetLow,
        Double targetHigh,
        String bloodType,
        Integer heightCm,
        Double hba1c
) {
}
