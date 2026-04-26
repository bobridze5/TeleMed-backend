package com.bobridze5.TeleMed_backend.core.service.profile;

import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileResponse;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.profile.PatientProfileMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientProfileService {
    private final UserService userService;
    private final PatientProfileMapper patientProfileMapper;

    // TODO: исправить двойной запрос
    @Transactional(readOnly = true)
    public PatientProfileResponse getProfile(Patient patient) {
        log.info("Получение данных профиля пользователя с id = {}", patient.getId());
        return patientProfileMapper.mapToResponse(patient);
    }

    @Transactional
    public PatientProfileResponse updateProfile(Patient patient, PatientProfileUpdateRequest request) {
        userService.updateProfile(patient, request);
        patientProfileMapper.updateProfile(patient, request);

        return patientProfileMapper.mapToResponse(patient);
    }
}
