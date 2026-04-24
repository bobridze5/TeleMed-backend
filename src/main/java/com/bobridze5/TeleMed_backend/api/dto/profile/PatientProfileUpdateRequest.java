package com.bobridze5.TeleMed_backend.api.dto.profile;

import com.bobridze5.TeleMed_backend.core.entity.medical.DiabetesType;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientProfileUpdateRequest extends UserProfileUpdateRequest {
    private DiabetesType diabetesType;

    @PastOrPresent(message = "Дата может не может быть в будущем")
    private LocalDate diagnosisDate;

    private Boolean isInsulinDependency;

    @Positive
    private Double targetLow;

    @Positive
    private Double targetHigh;

    private String bloodType;

    @Positive
    private Integer heightCm;

    @Positive
    private Double hba1c;
}