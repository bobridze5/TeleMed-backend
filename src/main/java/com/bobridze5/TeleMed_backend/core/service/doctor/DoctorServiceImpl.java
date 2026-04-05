package com.bobridze5.TeleMed_backend.core.service.doctor;

import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.api.mappers.doctor.DoctorProfileMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientDoctorAssignmentRepository;
import com.bobridze5.TeleMed_backend.core.service.auth.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final DoctorProfileMapper doctorProfileMapper;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Врач не найден"));
        return doctorProfileMapper.mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> getDoctors(DoctorFilterRequest filter, Pageable pageable) {
        return doctorRepository
                .findAllFiltered(filter.getSpecializationId(), filter.getCityId(), pageable)
                .map(doctorProfileMapper::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getMyProfile(Doctor doctor) {
        return doctorProfileMapper.mapToResponse(doctor);
    }

    @Override
    @Transactional
    public DoctorResponse updateMyProfile(Doctor doctor, DoctorProfileUpdateRequest request) {
        userService.updateProfile(doctor, request);
        doctorProfileMapper.updateDoctor(doctor, request);
        return doctorProfileMapper.mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorPatientResponse> getMyPatients(Doctor doctor, Pageable pageable) {
        log.info("Получение списка пациентов врача id={}", doctor.getId());
        return assignmentRepository
                .findActiveByDoctorId(doctor.getId(), pageable)
                .map(doctorProfileMapper::mapToPatientResponse);
    }
}
