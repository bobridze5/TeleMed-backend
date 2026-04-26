package com.bobridze5.TeleMed_backend.core.service.medcard;

import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicationRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicationResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Medication;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationRepository medicationRepository;

    public Page<MedicationResponse> getAll(Patient patient, int page, int size) {
        return medicationRepository
                .findByPatientId(patient.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    @Transactional
    public MedicationResponse create(Patient patient, MedicationRequest request) {
        Medication med = Medication.builder()
                .patient(patient)
                .name(request.name())
                .dosage(request.dosage())
                .frequency(request.frequency())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .active(request.active() != null ? request.active() : true)
                .build();
        return toResponse(medicationRepository.save(med));
    }

    @Transactional
    public MedicationResponse update(Patient patient, Long id, MedicationRequest request) {
        Medication med = medicationRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Препарат не найден"));
        if (request.name() != null) med.setName(request.name());
        if (request.dosage() != null) med.setDosage(request.dosage());
        if (request.frequency() != null) med.setFrequency(request.frequency());
        if (request.startDate() != null) med.setStartDate(request.startDate());
        if (request.endDate() != null) med.setEndDate(request.endDate());
        if (request.active() != null) med.setActive(request.active());
        return toResponse(med);
    }

    @Transactional
    public void delete(Patient patient, Long id) {
        Medication med = medicationRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Препарат не найден"));
        medicationRepository.delete(med);
    }

    private MedicationResponse toResponse(Medication m) {
        return new MedicationResponse(m.getId(), m.getName(), m.getDosage(),
                m.getFrequency(), m.getStartDate(), m.getEndDate(), m.getActive(), m.getCreatedAt());
    }
}
