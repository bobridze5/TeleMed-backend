package com.bobridze5.TeleMed_backend.core.service.profile;

import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Deprecated
public class DoctorProfile implements Profile {
    private final DoctorRepository doctorRepository;

    @Override
    public UserRole getRole() {
        return UserRole.DOCTOR;
    }

    @Override
    public void createProfile(User user) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
//        user.setDoctor(doctor);
        doctorRepository.save(doctor);
    }

}
