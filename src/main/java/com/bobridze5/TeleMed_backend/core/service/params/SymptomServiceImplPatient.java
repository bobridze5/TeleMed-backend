package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.params.SymptomMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.symptom.Symptom;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.SymptomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SymptomServiceImplPatient implements SymptomServicePatient {
    private final SymptomRepository symptomRepository;
    private final SymptomMapper symptomMapper;

    @Override
    @Transactional
    public SymptomResponse addRecord(Patient patient, SymptomRequest request) {
        Symptom symptom = symptomMapper.mapToEntity(request, patient);
        Symptom saved = symptomRepository.save(symptom);
        return symptomMapper.mapToResponse(saved);
    }

    @Override
    @Transactional
    public SymptomResponse updateRecord(Patient patient, Long id, SymptomUpdateRequest request) {
        Symptom symptom = symptomRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        symptomMapper.updateEntity(request, symptom);
        return symptomMapper.mapToResponse(symptom);
    }

    @Override
    public SymptomResponse getRecordById(Patient patient, Long id) {
        Symptom symptom = symptomRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        return symptomMapper.mapToResponse(symptom);
    }

    @Override
    public Page<SymptomResponse> getRecords(Patient patient, SymptomFilterRequest filter) {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null && filter.size() > 0 ? filter.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return symptomRepository.findWithFilter(
                patient.getId(),
                filter.severity(),
                filter.startDate(),
                filter.endDate(),
                pageable
        ).map(symptomMapper::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteRecord(Patient patient, Long id) {
        Symptom symptom = symptomRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        symptomRepository.delete(symptom);
    }
}
