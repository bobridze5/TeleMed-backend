package com.bobridze5.TeleMed_backend.api.dto.medcard;

import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileResponse;

import java.util.List;

public record MedicalCardResponse(
        PatientProfileResponse profile,
        List<AllergyResponse> allergies,
        List<MedicationResponse> medications,
        List<MedicalEventResponse> medicalEvents,
        List<MedicalRecordResponse> recentRecords
) {
}
