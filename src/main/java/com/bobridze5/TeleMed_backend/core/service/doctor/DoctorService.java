package com.bobridze5.TeleMed_backend.core.service.doctor;

import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.api.mappers.doctor.DoctorProfileMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientDoctorAssignmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import com.bobridze5.TeleMed_backend.core.repository.ReviewRepository;
import com.bobridze5.TeleMed_backend.core.service.auth.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final PatientRepository patientRepository;
    private final ReviewRepository reviewRepository;
    private final DoctorProfileMapper doctorProfileMapper;
    private final UserService userService;

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Врач не найден"));
        Object[] stats = firstStatsRow(reviewRepository.findRatingStatsByDoctorId(id));
        Double avg = toDouble(stats, 0);
        Long count = toLong(stats, 1);
        return doctorProfileMapper.mapToResponse(doctor, avg, count);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> getDoctors(DoctorFilterRequest filter, Pageable pageable) {
        Page<Doctor> page = doctorRepository
                .findAllFiltered(filter.getSpecializationId(), filter.getCityId(), pageable);

        // Одним запросом подтягиваем агрегат отзывов по всем врачам страницы,
        // чтобы не делать N+1 при отдаче списка.
        List<Long> ids = page.getContent().stream().map(Doctor::getId).toList();
        Map<Long, Double> avgById = new HashMap<>();
        Map<Long, Long> countById = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Object[] row : reviewRepository.findRatingStatsByDoctorIds(ids)) {
                Long doctorId = ((Number) row[0]).longValue();
                Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : null;
                Long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                avgById.put(doctorId, avg);
                countById.put(doctorId, count);
            }
        }

        return page.map(d -> doctorProfileMapper.mapToResponse(
                d, avgById.get(d.getId()), countById.getOrDefault(d.getId(), 0L)));
    }

    @Transactional(readOnly = true)
    public DoctorResponse getMyProfile(Doctor doctor) {
        Object[] stats = firstStatsRow(reviewRepository.findRatingStatsByDoctorId(doctor.getId()));
        return doctorProfileMapper.mapToResponse(doctor, toDouble(stats, 0), toLong(stats, 1));
    }

    @Transactional
    public DoctorResponse updateMyProfile(Doctor doctor, DoctorProfileUpdateRequest request) {
        userService.updateProfile(doctor, request);
        doctorProfileMapper.updateDoctor(doctor, request);
        Object[] stats = firstStatsRow(reviewRepository.findRatingStatsByDoctorId(doctor.getId()));
        return doctorProfileMapper.mapToResponse(doctor, toDouble(stats, 0), toLong(stats, 1));
    }

    @Transactional(readOnly = true)
    public Page<DoctorPatientResponse> getMyPatients(Doctor doctor, Pageable pageable) {
        log.info("Получение списка пациентов врача id={}", doctor.getId());
        return assignmentRepository
                .findActiveByDoctorId(doctor.getId(), pageable)
                .map(doctorProfileMapper::mapToPatientResponse);
    }

    @Transactional(readOnly = true)
    public Page<DoctorPatientResponse> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(doctorProfileMapper::mapPatientToResponse);
    }

    // --- helpers ---

    private static Object[] firstStatsRow(List<Object[]> rows) {
        return (rows == null || rows.isEmpty()) ? null : rows.get(0);
    }

    private static Double toDouble(Object[] row, int idx) {
        if (row == null || idx >= row.length || row[idx] == null) return null;
        return ((Number) row[idx]).doubleValue();
    }

    private static Long toLong(Object[] row, int idx) {
        if (row == null || idx >= row.length || row[idx] == null) return 0L;
        return ((Number) row[idx]).longValue();
    }
}
