package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;
import com.bobridze5.TeleMed_backend.api.mappers.params.BloodPressureMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.pressure.BloodPressure;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.BloodPressureRepository;
import com.bobridze5.TeleMed_backend.core.service.utils.ParamSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BloodPressureServiceImplPatient implements BloodPressureServicePatient {
    private final BloodPressureRepository bloodPressureRepository;

    @Override
    @Transactional
    public BloodPressureResponse addRecord(Patient patient, BloodPressureRequest request) {
        BloodPressure bloodPressure = BloodPressureMapper.mapToEntity(request, patient);
        BloodPressure savedBloodPressure = bloodPressureRepository.save(bloodPressure);

        return BloodPressureMapper.mapToResponse(savedBloodPressure);
    }

    @Override
    @Transactional
    public BloodPressureResponse updateRecord(Patient patient, Long id, BloodPressureRequest request) {
        BloodPressure bloodPressure = bloodPressureRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found")
        );

        if (!patient.isOwner(bloodPressure)) {
            throw new AccessDeniedException("Forbidden");
        }

        BloodPressureMapper.updateEntity(request, bloodPressure);

        return BloodPressureMapper.mapToResponse(bloodPressure);
    }

    @Override
    @Transactional(readOnly = true)
    public BloodPressureResponse getRecordById(Patient patient, Long id) {
        BloodPressure bloodPressure = bloodPressureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (!patient.isOwner(bloodPressure)) {
            throw new AccessDeniedException("Forbidden");
        }

        return BloodPressureMapper.mapToResponse(bloodPressure);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BloodPressureResponse> getRecords(Patient patient, BloodPressureFilterRequest request) {
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null && request.size() > 0 ? request.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<BloodPressure> spec = ParamSpecification.byPatientAndDateTimeBetween(
                patient.getId(), request
        );

        return bloodPressureRepository
                .findAll(spec, pageable)
                .map(BloodPressureMapper::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteRecord(Patient patient, Long id) {
        BloodPressure bloodPressure = bloodPressureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (!patient.isOwner(bloodPressure)) {
            throw new AccessDeniedException("Forbidden");
        }

        bloodPressureRepository.delete(bloodPressure);
    }
}
