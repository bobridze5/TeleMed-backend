package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;

public interface RegisterService {
    RegisterResponse register(RegisterPatientRequest request, String url);

    RegisterResponse register(RegisterDoctorRequest request);

    RegisterResponse register(RegisterAdminRequest request);
}
