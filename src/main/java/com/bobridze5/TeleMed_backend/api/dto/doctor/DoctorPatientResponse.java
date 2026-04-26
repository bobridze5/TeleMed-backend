package com.bobridze5.TeleMed_backend.api.dto.doctor;

import com.bobridze5.TeleMed_backend.core.entity.medical.DiabetesType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DoctorPatientResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        DiabetesType diabetesType,
        LocalDate diagnosisDate,
        LocalDateTime assignedAt
) {
}
