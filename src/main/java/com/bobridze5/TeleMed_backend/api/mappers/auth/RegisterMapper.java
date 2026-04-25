package com.bobridze5.TeleMed_backend.api.mappers.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterMapper {
    private final BCryptPasswordEncoder passwordEncoder;

    public Patient mapToInitialPatient(RegisterPatientRequest request) {
        return Patient.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password1()))
                .status(UserStatus.PENDING)
                .build();
    }

    public Doctor mapToInitialDoctor(RegisterDoctorRequest request, Specialization specialization,
                                     @Nullable MedicalOrganization organization) {
        return Doctor.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password1()))
                .status(UserStatus.AWAITING_APPROVAL)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .specialization(specialization)
                .organization(organization)
                .experience(request.experience())
                .qualification(request.qualification())
                .build();
    }

    public Admin mapToInitialAdmin(RegisterAdminRequest request) {
        return Admin.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password1()))
                .status(UserStatus.AWAITING_APPROVAL)
                .build();
    }
}
