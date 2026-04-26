package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.params.PhysicalActivityMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivity;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.PhysicalActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhysicalActivityServiceImpl implements PhysicalActivityService {
    private final PhysicalActivityRepository physicalActivityRepository;
    private final PhysicalActivityMapper physicalActivityMapper;

    @Override
    @Transactional
    public PhysicalActivityResponse addRecord(Patient patient, PhysicalActivityRequest request) {
        PhysicalActivity activity = physicalActivityMapper.mapToEntity(request, patient);
        PhysicalActivity savedActivity = physicalActivityRepository.save(activity);
        return physicalActivityMapper.mapToResponse(savedActivity);
    }

    @Override
    @Transactional
    public PhysicalActivityResponse updateRecord(Patient patient, Long id, PhysicalActivityUpdateRequest request) {
        PhysicalActivity activity = physicalActivityRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        physicalActivityMapper.updateEntity(request, activity);
        return physicalActivityMapper.mapToResponse(activity);
    }

    @Override
    public PhysicalActivityResponse getRecordById(Patient patient, Long id) {
        PhysicalActivity activity = physicalActivityRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        return physicalActivityMapper.mapToResponse(activity);
    }

    @Override
    public Page<PhysicalActivityResponse> getRecords(Patient patient, PhysicalActivityFilterRequest filter) {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null && filter.size() > 0 ? filter.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return physicalActivityRepository.findWithFilter(
                patient.getId(),
                filter.activityType(),
                filter.intensityType(),
                filter.duration(),
                filter.startDate(),
                filter.endDate(),
                pageable
        ).map(physicalActivityMapper::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteRecord(Patient patient, Long id) {
        PhysicalActivity activity = physicalActivityRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        physicalActivityRepository.delete(activity);
    }
}
