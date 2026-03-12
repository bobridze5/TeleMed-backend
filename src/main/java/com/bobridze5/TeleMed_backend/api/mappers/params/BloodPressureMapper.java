package com.bobridze5.TeleMed_backend.api.mappers.params;

import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;
import com.bobridze5.TeleMed_backend.core.entity.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.BloodPressure;

public class BloodPressureMapper {
    public static BloodPressure mapToEntity(BloodPressureRequest request, Patient patient) {
        return BloodPressure.builder()
                .id(null)
                .patient(patient)
                .systolic(request.systolic())
                .diastolic(request.diastolic())
                .build();
    }

    public static BloodPressureResponse mapToResponse(BloodPressure bloodPressure) {
        return new BloodPressureResponse(
                bloodPressure.getId(),
                bloodPressure.getSystolic(),
                bloodPressure.getDiastolic(),
                bloodPressure.getCreatedAt()
        );
    }

    public static void updateEntity(BloodPressureRequest request, BloodPressure bloodPressure) {
        if (request.systolic() != null) bloodPressure.setSystolic(request.systolic());
        if (request.diastolic() != null) bloodPressure.setDiastolic(request.diastolic());
    }
}
