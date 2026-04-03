package com.bobridze5.TeleMed_backend.api.mappers.params;

import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Weight;

public class WeightMapper {
    public static Weight mapToEntity(WeightRequest request, Patient patient) {
        return Weight.builder()
                .id(null)
                .value(request.value())
                .patient(patient)
                .build();
    }

    public static WeightResponse mapToResponse(Weight weight) {
        return new WeightResponse(
                weight.getId(),
                weight.getValue(),
                weight.getCreatedAt()
        );
    }

    public static void updateEntity(WeightRequest request, Weight weight) {
        if (request.value() != null) weight.setValue(request.value());
    }
}
