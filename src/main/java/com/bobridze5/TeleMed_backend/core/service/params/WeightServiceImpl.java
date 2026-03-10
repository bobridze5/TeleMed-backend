package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.api.mappers.WeightMapper;
import com.bobridze5.TeleMed_backend.core.entity.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Weight;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.WeightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeightServiceImpl implements WeightService {
    private final WeightRepository weightRepository;

    @Override
    @Transactional
    public WeightResponse addWeightRecord(Patient patient, WeightRequest request) {
        Weight weight = WeightMapper.mapToEntity(request, patient);
        Weight savedWeight = weightRepository.save(weight);

        return WeightMapper.mapToResponse(savedWeight);
    }

    @Override
    public WeightResponse getWeightById(Patient patient, Long weightId) {
        Weight weight = weightRepository.findById(weightId).orElseThrow(
                () -> new EntityNotFoundException("Not found")
        );

        if (!patient.isOwner(weight)) {
            throw new AccessDeniedException("Forbidden");
        }

        return WeightMapper.mapToResponse(weight);
    }


    @Override
    public WeightResponse updateWeight(WeightRequest request, Patient patient, Long weightId) {
        Weight weight = weightRepository.findById(weightId).orElseThrow(
                () -> new EntityNotFoundException("Not found")
        );

        if (!patient.isOwner(weight)) {
            throw new AccessDeniedException("Forbidden");
        }

        WeightMapper.updateEntity(request, weight);

        return WeightMapper.mapToResponse(weight);
    }

    @Override
    public void deleteWeightRecord(Patient patient, Long weightId) {
        Weight weight = weightRepository.findById(weightId).orElseThrow(
                () -> new EntityNotFoundException("Not found")
        );

        if (!patient.isOwner(weight)) {
            throw new AccessDeniedException("");
        }

        weightRepository.delete(weight);
    }
}
