package com.bobridze5.TeleMed_backend.core.service.doctor;

import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    DoctorResponse getDoctorById(Long id);
    Page<DoctorResponse> getDoctors(DoctorFilterRequest filter, Pageable pageable);
    DoctorResponse getMyProfile(Doctor doctor);
    DoctorResponse updateMyProfile(Doctor doctor, DoctorProfileUpdateRequest request);
    Page<DoctorPatientResponse> getMyPatients(Doctor doctor, Pageable pageable);
}
