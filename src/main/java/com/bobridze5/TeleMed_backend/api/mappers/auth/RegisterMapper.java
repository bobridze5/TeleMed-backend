package com.bobridze5.TeleMed_backend.api.mappers.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;

public interface RegisterMapper {
    Patient mapToInitialPatient(RegisterPatientRequest request);

    Doctor mapToInitialDoctor(RegisterDoctorRequest request);

//    void updatePatientFromProfile(Patient patient, PatientProfileRequest dto);
//    void updateDoctorFromProfile(Doctor doctor, DoctorProfileRequest dto);

    RegisterResponse mapToResponse(User user);
}
