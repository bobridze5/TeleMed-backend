package com.bobridze5.TeleMed_backend.api.mappers.params;

import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.symptom.Symptom;
import org.springframework.stereotype.Component;

@Component
public class SymptomMapper {
    public Symptom mapToEntity(SymptomRequest request, Patient patient) {
        return Symptom.builder()
                .id(null)
                .patient(patient)
                .severity(request.severity())
                .description(request.description())
                .build();
    }

    public SymptomResponse mapToResponse(Symptom entity) {
        return new SymptomResponse(
                entity.getId(),
                entity.getSeverity(),
                entity.getDescription(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntity(SymptomUpdateRequest request, Symptom entity) {
        if (request.severity() != null) entity.setSeverity(request.severity());
        if (request.description() != null) entity.setDescription(request.description());
    }
}
