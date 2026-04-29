package com.bobridze5.TeleMed_backend.core.service.admin;

import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorPendingResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminStatsResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.api.mappers.doctor.DoctorProfileMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import com.bobridze5.TeleMed_backend.core.service.auth.EmailService;
import com.bobridze5.TeleMed_backend.core.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DoctorProfileMapper doctorProfileMapper;
    private final EmailService emailService;
    private final UserNotificationService userNotificationService;

    @Transactional(readOnly = true)
    public Page<AdminDoctorPendingResponse> getPendingDoctors(Pageable pageable) {
        return doctorRepository.findByStatus(UserStatus.AWAITING_APPROVAL, pageable)
                .map(this::mapToPendingResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdminDoctorResponse> getAllDoctors(UserStatus status, Pageable pageable) {
        Page<Doctor> page = (status != null)
                ? doctorRepository.findByStatus(status, pageable)
                : doctorRepository.findAll(pageable);
        return page.map(this::mapToDoctorResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdminPatientResponse> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(this::mapToPatientResponse);
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long totalPatients = patientRepository.count();
        long totalDoctors = doctorRepository.count();
        long pendingDoctors = doctorRepository.countByStatus(UserStatus.AWAITING_APPROVAL);
        long activeDoctors = doctorRepository.countByStatus(UserStatus.ACTIVE);
        long bannedUsers = userRepository.countByStatus(UserStatus.BANNED);
        return new AdminStatsResponse(totalPatients, totalDoctors, pendingDoctors, activeDoctors, bannedUsers);
    }

    @Transactional
    public DoctorResponse approveDoctor(Long doctorId) {
        Doctor doctor = findAwaitingDoctor(doctorId);
        doctor.setStatus(UserStatus.ACTIVE);
        log.info("Врач id={} одобрен администратором", doctorId);
        emailService.sendDoctorApproved(doctor.getEmail(), doctor.getLastName());

        userNotificationService.create(
                doctor,
                null,
                "DOCTOR_APPROVAL",
                "Профиль одобрен",
                "Ваша заявка рассмотрена и одобрена администрацией TeleMed. " +
                        "Теперь вы можете принимать пациентов. Добро пожаловать!");

        return doctorProfileMapper.mapToResponse(doctor);
    }

    @Transactional
    public void rejectDoctor(Long doctorId, String reason) {
        Doctor doctor = findAwaitingDoctor(doctorId);
        doctor.setStatus(UserStatus.INACTIVE);
        log.info("Врач id={} отклонён администратором. Причина: {}", doctorId, reason);
        emailService.sendDoctorRejected(doctor.getEmail(), doctor.getLastName(), reason);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        user.setStatus(UserStatus.BANNED);
        log.info("Пользователь id={} заблокирован администратором", userId);
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        user.setStatus(UserStatus.ACTIVE);
        log.info("Пользователь id={} активирован администратором", userId);
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

    private AdminDoctorResponse mapToDoctorResponse(Doctor doctor) {
        return new AdminDoctorResponse(
                doctor.getId(),
                doctor.getEmail(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getMiddleName(),
                doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : null,
                doctor.getOrganization() != null ? doctor.getOrganization().getName() : null,
                doctor.getExperience(),
                doctor.getQualification(),
                doctor.getStatus(),
                doctor.getCreatedAt()
        );
    }

    private AdminPatientResponse mapToPatientResponse(Patient patient) {
        return new AdminPatientResponse(
                patient.getId(),
                patient.getEmail(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getMiddleName(),
                patient.getStatus(),
                patient.getCreatedAt()
        );
    }
}
