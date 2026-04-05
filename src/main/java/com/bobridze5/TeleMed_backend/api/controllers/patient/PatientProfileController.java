package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileResponse;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.profile.PatientProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_PROFILE)
@RequiredArgsConstructor
@Tag(name = "Профиль пациента")
public class PatientProfileController {
    private final PatientProfileService profileService;

    @GetMapping
    @Operation(summary = "Получить профиль пациента", description = "Возвращает личные данные и медицинские параметры текущего пациента")
    public PatientProfileResponse getProfile(@CurrentPatient Patient patient) {
        return profileService.getProfile(patient);
    }

    @PatchMapping
    @Operation(summary = "Обновить профиль пациента", description = "Изменяет личные данные и медицинские параметры текущего пациента")
    public PatientProfileResponse updateProfile(
            @CurrentPatient Patient patient,
            @Valid @RequestBody PatientProfileUpdateRequest request
    ) {
        return profileService.updateProfile(patient, request);
    }

}
