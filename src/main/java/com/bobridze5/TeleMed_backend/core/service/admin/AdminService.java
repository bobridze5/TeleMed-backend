package com.bobridze5.TeleMed_backend.core.service.admin;

import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorPendingResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<AdminDoctorPendingResponse> getPendingDoctors(Pageable pageable);

    DoctorResponse approveDoctor(Long doctorId);

    void rejectDoctor(Long doctorId, String reason);
}
