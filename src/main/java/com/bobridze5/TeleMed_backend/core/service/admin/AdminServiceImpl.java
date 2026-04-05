package com.bobridze5.TeleMed_backend.core.service.admin;

import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorPendingResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.api.mappers.doctor.DoctorProfileMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
import com.bobridze5.TeleMed_backend.core.service.auth.EmailService;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final DoctorRepository doctorRepository;
    private final DoctorProfileMapper doctorProfileMapper;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminDoctorPendingResponse> getPendingDoctors(Pageable pageable) {
        return doctorRepository.findByStatus(UserStatus.AWAITING_APPROVAL, pageable)
                .map(this::mapToPendingResponse);
    }

    @Override
    @Transactional
    public DoctorResponse approveDoctor(Long doctorId) {
        Doctor doctor = findAwaitingDoctor(doctorId);
        doctor.setStatus(UserStatus.ACTIVE);
        log.info("Врач id={} одобрен администратором", doctorId);
        emailService.sendDoctorApproved(doctor.getEmail(), doctor.getLastName());
        return doctorProfileMapper.mapToResponse(doctor);
    }

    @Override
    @Transactional
    public void rejectDoctor(Long doctorId, String reason) {
        Doctor doctor = findAwaitingDoctor(doctorId);
        doctor.setStatus(UserStatus.INACTIVE);
        log.info("Врач id={} отклонён администратором. Причина: {}", doctorId, reason);
        emailService.sendDoctorRejected(doctor.getEmail(), doctor.getLastName(), reason);
    }

    private Doctor findAwaitingDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Врач не найден"));
        if (doctor.getStatus() != UserStatus.AWAITING_APPROVAL) {
            throw new IllegalStateException("Врач не находится в статусе ожидания проверки");
        }
        return doctor;
    }

    private AdminDoctorPendingResponse mapToPendingResponse(Doctor doctor) {
        return new AdminDoctorPendingResponse(
                doctor.getId(),
                doctor.getEmail(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getMiddleName(),
                doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : null,
                doctor.getOrganization() != null ? doctor.getOrganization().getName() : null,
                doctor.getExperience(),
                doctor.getQualification(),
                doctor.getCreatedAt()
        );
    }
}
