package com.bobridze5.TeleMed_backend.core.service.admin;

import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorPendingResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminStatsResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<AdminDoctorPendingResponse> getPendingDoctors(Pageable pageable);

    Page<AdminDoctorResponse> getAllDoctors(UserStatus status, Pageable pageable);

    Page<AdminPatientResponse> getAllPatients(Pageable pageable);

    AdminStatsResponse getStats();

    DoctorResponse approveDoctor(Long doctorId);

    void rejectDoctor(Long doctorId, String reason);

    void banUser(Long userId);

    void activateUser(Long userId);
}
