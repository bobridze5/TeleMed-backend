package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.api.mappers.params.WeightMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Weight;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.WeightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeightServiceImpl implements WeightService {
    private final WeightRepository weightRepository;

    @Override
    public Page<WeightResponse> getWeightRecords(Patient patient, WeightFilterRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size(), Sort.by("createdAt").descending());

        Page<Weight> weightPage;

        Long patientId = patient.getId();

        if (request.isBetween()) {
            weightPage = weightRepository.findByPatientIdAndCreatedAtBetween(
                    patientId, request.startDate(), request.endDate(), pageable
            );
        } else {
            weightPage = weightRepository.findByPatientId(patientId, pageable);
        }

        return weightPage.map(WeightMapper::mapToResponse);
    }

    @Override
    @Transactional
    public WeightResponse addWeightRecord(Patient patient, WeightRequest request) {
        Weight weight = WeightMapper.mapToEntity(request, patient);
        Weight savedWeight = weightRepository.save(weight);
        return WeightMapper.mapToResponse(savedWeight);
    }

    @Override
    public WeightResponse getWeightRecordById(Patient patient, Long weightId) {
        Weight weight = weightRepository.findByIdAndPatientId(weightId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        return WeightMapper.mapToResponse(weight);
    }


    @Override
    @Transactional
    public WeightResponse updateWeightRecord(Patient patient, Long weightId, WeightRequest request) {
        Weight weight = weightRepository.findByIdAndPatientId(weightId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        WeightMapper.updateEntity(request, weight);

        return WeightMapper.mapToResponse(weight);
    }

    @Override
    @Transactional
    public void deleteWeightRecord(Patient patient, Long weightId) {
        Weight weight = weightRepository.findByIdAndPatientId(weightId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        weightRepository.delete(weight);
    }
}
