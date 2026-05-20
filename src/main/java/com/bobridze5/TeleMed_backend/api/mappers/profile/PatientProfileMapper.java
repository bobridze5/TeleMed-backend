package com.bobridze5.TeleMed_backend.api.mappers.profile;

import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileResponse;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientProfileMapper {
    public PatientProfileResponse mapToResponse(Patient patient) {
        return new PatientProfileResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getMiddleName(),
                patient.getDateOfBirth(),
                patient.getGender() != null ? String.valueOf(patient.getGender()) : null,
                patient.getEmail(),
                patient.getDiabetesType(),
                patient.getDiagnosisDate(),
                patient.getIsInsulinDependency(),
                patient.getTargetLow(),
                patient.getTargetHigh(),
                patient.getBloodType(),
                patient.getHeightCm(),
                patient.getHba1c(),
                patient.getDailyCaloriesGoal(),
                patient.getDailyProteinGoal(),
                patient.getDailyFatsGoal(),
                patient.getDailyCarbsGoal()
        );
    }


    public void updateProfile(Patient patient, PatientProfileUpdateRequest request) {
        if (request.getIsInsulinDependency() != null) patient.setIsInsulinDependency(request.getIsInsulinDependency());
        if (request.getDiabetesType() != null) patient.setDiabetesType(request.getDiabetesType());
        if (request.getDiagnosisDate() != null) patient.setDiagnosisDate(request.getDiagnosisDate());
        if (request.getTargetHigh() != null) patient.setTargetHigh(request.getTargetHigh());
        if (request.getTargetLow() != null) patient.setTargetLow(request.getTargetLow());
        if (request.getBloodType() != null) patient.setBloodType(request.getBloodType());
        if (request.getHeightCm() != null) patient.setHeightCm(request.getHeightCm());
        if (request.getHba1c() != null) patient.setHba1c(request.getHba1c());
        if (request.getDailyCaloriesGoal() != null) patient.setDailyCaloriesGoal(request.getDailyCaloriesGoal());
        if (request.getDailyProteinGoal() != null) patient.setDailyProteinGoal(request.getDailyProteinGoal());
        if (request.getDailyFatsGoal() != null) patient.setDailyFatsGoal(request.getDailyFatsGoal());
        if (request.getDailyCarbsGoal() != null) patient.setDailyCarbsGoal(request.getDailyCarbsGoal());
    }
}
