package com.bobridze5.TeleMed_backend.core.service.medcard;

import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalEventRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalEventResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.MedicalEvent;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.MedicalEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalEventService {
    private final MedicalEventRepository medicalEventRepository;

    public Page<MedicalEventResponse> getAll(Patient patient, int page, int size) {
        return medicalEventRepository
                .findByPatientIdOrderByEventDateDesc(patient.getId(), PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional
    public MedicalEventResponse create(Patient patient, MedicalEventRequest request) {
        MedicalEvent event = MedicalEvent.builder()
                .patient(patient)
                .description(request.description())
                .eventDate(request.eventDate())
                .eventType(request.eventType())
                .endDate(request.endDate())
                .build();
        return toResponse(medicalEventRepository.save(event));
    }

    @Transactional
    public void delete(Patient patient, Long id) {
        MedicalEvent event = medicalEventRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
        medicalEventRepository.delete(event);
    }

    private MedicalEventResponse toResponse(MedicalEvent e) {
        return new MedicalEventResponse(e.getId(), e.getDescription(), e.getEventDate(), e.getEventType(), e.getEndDate(), e.getCreatedAt());
    }
}
