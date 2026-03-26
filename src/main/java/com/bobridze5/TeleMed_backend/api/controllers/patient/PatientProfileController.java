package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileResponse;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.profile.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_PROFILE)
@RequiredArgsConstructor
public class PatientProfileController {
    private final PatientProfileService profileService;

    @GetMapping
    public PatientProfileResponse getProfile(@CurrentPatient Patient patient) {
        return profileService.getProfile(patient);
    }

    @PatchMapping
    public PatientProfileResponse updateProfile(
            @CurrentPatient Patient patient,
            @RequestBody PatientProfileUpdateRequest request
    ) {
        return profileService.updateProfile(patient, request);
    }

}
