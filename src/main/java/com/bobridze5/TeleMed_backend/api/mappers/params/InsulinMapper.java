package com.bobridze5.TeleMed_backend.api.mappers.params;

import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinDose;
import org.springframework.stereotype.Component;

@Component
public final class InsulinMapper {
    public static InsulinDose mapToEntity(InsulinDoseRequest request, Patient patient, Meal meal) {
        return InsulinDose.builder()
                .id(null)
                .meal(meal)
                .patient(patient)
                .takenAt(request.takenAt())
                .units(request.units())
                .insulinType(request.insulinType())
                .note(request.note())
                .build();
    }

    public static InsulinDoseResponse mapToResponse(InsulinDose insulinDose) {
        return new InsulinDoseResponse(
                insulinDose.getId(),
                insulinDose.getMeal() != null ? insulinDose.getMeal().getId() : null,
                insulinDose.getUnits(),
                insulinDose.getInsulinType(),
                insulinDose.getNote(),
                insulinDose.getTakenAt(),
                insulinDose.getCreatedAt(),
                insulinDose.getUpdatedAt()
        );
    }

    public static void updateEntity(InsulinDoseUpdateRequest request, InsulinDose insulinDose, Meal meal) {
        if (request.units() != null) insulinDose.setUnits(request.units());
        if (request.insulinType() != null) insulinDose.setInsulinType(request.insulinType());
        if (request.takenAt() != null) insulinDose.setTakenAt(request.takenAt());
        if (request.note() != null) insulinDose.setNote(request.note());

        insulinDose.setMeal(meal);
    }
}
