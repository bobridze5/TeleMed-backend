package com.bobridze5.TeleMed_backend.api.mappers.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.*;
import org.springframework.lang.Nullable;

public interface RegisterMapper {
    Patient mapToInitialPatient(RegisterPatientRequest request);

    Doctor mapToInitialDoctor(RegisterDoctorRequest request, Specialization specialization, @Nullable MedicalOrganization organization);

    Admin mapToInitialAdmin(RegisterAdminRequest request);

    RegisterResponse mapToResponse(User user);
}
