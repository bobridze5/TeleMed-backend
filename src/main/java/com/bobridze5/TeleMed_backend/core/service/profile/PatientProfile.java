package com.bobridze5.TeleMed_backend.core.service.profile;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Deprecated
public class PatientProfile implements Profile {
    private final PatientRepository patientRepository;

    @Override
    public UserRole getRole() {
        return UserRole.PATIENT;
    }

    @Override
    public void createProfile(User user) {
        Patient patient = Patient.builder()
                .user(user)
                .build();

//        user.setPatient(patient);
        patientRepository.save(patient);
    }
}
