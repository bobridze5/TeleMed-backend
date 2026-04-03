package com.bobridze5.TeleMed_backend.api.mappers.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterMapperImpl implements RegisterMapper {
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Patient mapToInitialPatient(RegisterPatientRequest request) {
        return Patient.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password1()))
                .status(UserStatus.PENDING)
                .build();
    }

    // TODO: добавить другие реализации

    @Override
    public Doctor mapToInitialDoctor(RegisterDoctorRequest request) {
        return new Doctor();
    }

    @Override
    public RegisterResponse mapToResponse(User user) {
        return new RegisterResponse(user.getId());
    }
}
