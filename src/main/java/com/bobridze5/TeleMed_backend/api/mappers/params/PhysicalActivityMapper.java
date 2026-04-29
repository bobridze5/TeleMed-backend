package com.bobridze5.TeleMed_backend.api.mappers.params;

import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivity;
import org.springframework.stereotype.Component;

@Component
public final class PhysicalActivityMapper {
    public PhysicalActivity mapToEntity(PhysicalActivityRequest request, Patient patient) {
        return PhysicalActivity.builder()
                .id(null)
                .patient(patient)
                .activityType(request.activityType())
                .intensityType(request.intensityType())
                .duration(request.duration())
                .createdAt(request.timestamp())
                .build();
    }

    public PhysicalActivityResponse mapToResponse(PhysicalActivity entity) {
        return new PhysicalActivityResponse(
                entity.getId(),
                entity.getActivityType(),
                entity.getIntensityType(),
                entity.getDuration(),
                entity.getCreatedAt()
        );
    }

    public void updateEntity(PhysicalActivityUpdateRequest request, PhysicalActivity entity) {
        if (request.activityType() != null) entity.setActivityType(request.activityType());
        if (request.intensityType() != null) entity.setIntensityType(request.intensityType());
        if (request.duration() != null) entity.setDuration(request.duration());
        if (request.timestamp() != null) entity.setCreatedAt(request.timestamp());
    }
}
