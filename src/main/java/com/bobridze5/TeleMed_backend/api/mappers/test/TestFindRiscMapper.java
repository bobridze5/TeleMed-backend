package com.bobridze5.TeleMed_backend.api.mappers.test;

import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscResponse;
import com.bobridze5.TeleMed_backend.core.entity.TestFindRisc;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import org.springframework.stereotype.Component;

@Component
public final class TestFindRiscMapper {
    public TestFindRiscResponse mapToResponse(TestFindRisc test) {
        return new TestFindRiscResponse(
                test.getId(),
                test.getAgePoints(),
                test.getBodyMassIndexPoints(),
                test.getWaistCircumferencePoints(),
                test.getVegetablePoints(),
                test.getActivityPoints(),
                test.getHypertensionPoints(),
                test.getGlucosePoints(),
                test.getFamilyHistoryPoints(),
                test.getTotalPoints(),
                test.getCreatedAt()
        );
    }

    public TestFindRisc mapToEntity(TestFindRiscRequest request, Patient patient) {
        int totalPoints = request.agePoints() + request.bodyMassIndexPoints() +
                request.waistCircumferencePoints() + request.vegetablePoints() +
                request.activityPoints() + request.hypertensionPoints() +
                request.glucosePoints() + request.familyHistoryPoints();

        return TestFindRisc.builder()
                .patient(patient)
                .agePoints(request.agePoints())
                .bodyMassIndexPoints(request.bodyMassIndexPoints())
                .waistCircumferencePoints(request.waistCircumferencePoints())
                .vegetablePoints(request.vegetablePoints())
                .activityPoints(request.activityPoints())
                .hypertensionPoints(request.hypertensionPoints())
                .glucosePoints(request.glucosePoints())
                .familyHistoryPoints(request.familyHistoryPoints())
                .totalPoints(totalPoints)
                .build();
    }
}
