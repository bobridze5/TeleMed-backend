package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.params.GlycemiaMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Glycemia;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.GlycemiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GlycemiaServiceImpl implements GlycemiaService {
    private final GlycemiaRepository glycemiaRepository;
    private final GlycemiaMapper glycemiaMapper;

    @Override
    @Transactional
    public GlycemiaResponse addRecord(Patient patient, GlycemiaRequest request) {
        Glycemia glycemia = glycemiaMapper.mapToEntity(request, patient);
        Glycemia savedGlycemia = glycemiaRepository.save(glycemia);
        return glycemiaMapper.mapToResponse(savedGlycemia);
    }

    @Override
    @Transactional
    public GlycemiaResponse updateRecord(Patient patient, Long id, GlycemiaUpdateRequest request) {
        Glycemia glycemia = glycemiaRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        glycemiaMapper.updateEntity(request, glycemia);
        return glycemiaMapper.mapToResponse(glycemia);
    }

    @Override
    public GlycemiaResponse getRecordById(Patient patient, Long id) {
        Glycemia glycemia = glycemiaRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        return glycemiaMapper.mapToResponse(glycemia);
    }

    @Override
    public Page<GlycemiaResponse> getRecords(Patient patient, GlycemiaFilterRequest filter) {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null && filter.size() > 0 ? filter.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        return glycemiaRepository.findWithFilter(
                patient.getId(),
                filter.type(),
                filter.level(),
                filter.start(),
                filter.end(),
                pageable
        ).map(glycemiaMapper::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteRecord(Patient patient, Long id) {
        Glycemia glycemia = glycemiaRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        glycemiaRepository.delete(glycemia);
    }
}
