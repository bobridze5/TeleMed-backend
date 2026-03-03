package com.bobridze5.TeleMed_backend.core.service.profile;

import com.bobridze5.TeleMed_backend.core.entity.Patient;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientProfile implements Profile {
    private final PatientRepository patientRepository;

    @Override
    public UserRole getRole() {
        return UserRole.PATIENT;
    }

    @Override
    public void createProfile(User user) {
        Patient patient = new Patient();
        patient.setUser(user);

        patientRepository.save(patient);
    }
}
