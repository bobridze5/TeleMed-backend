package com.bobridze5.TeleMed_backend.core.service.medcard;

import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalCardResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.profile.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalCardService {
    private static final int CARD_SECTION_PAGE_SIZE = 50;

    private final PatientProfileService profileService;
    private final AllergyService allergyService;
    private final MedicationService medicationService;
    private final MedicalEventService medicalEventService;
    private final MedicalRecordService medicalRecordService;

    @Transactional(readOnly = true)
    public MedicalCardResponse getCard(Patient patient) {
        return new MedicalCardResponse(
                profileService.getProfile(patient),
                allergyService.getAll(patient, 0, CARD_SECTION_PAGE_SIZE).getContent(),
                medicationService.getAll(patient, 0, CARD_SECTION_PAGE_SIZE).getContent(),
                medicalEventService.getAll(patient, 0, CARD_SECTION_PAGE_SIZE).getContent(),
                medicalRecordService.getLatestRecordsForPatient(patient)
        );
    }
}
