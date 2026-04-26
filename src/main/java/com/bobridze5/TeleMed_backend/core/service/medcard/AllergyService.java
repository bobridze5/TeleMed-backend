package com.bobridze5.TeleMed_backend.core.service.medcard;

import com.bobridze5.TeleMed_backend.api.dto.medcard.AllergyRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.AllergyResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Allergy;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AllergyService {
    private final AllergyRepository allergyRepository;

    public Page<AllergyResponse> getAll(Patient patient, int page, int size) {
        return allergyRepository
                .findByPatientId(patient.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    @Transactional
    public AllergyResponse create(Patient patient, AllergyRequest request) {
        Allergy allergy = Allergy.builder()
                .patient(patient)
                .allergen(request.allergen())
                .reaction(request.reaction())
                .build();
        return toResponse(allergyRepository.save(allergy));
    }

    @Transactional
    public void delete(Patient patient, Long id) {
        Allergy allergy = allergyRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Аллергия не найдена"));
        allergyRepository.delete(allergy);
    }

    private AllergyResponse toResponse(Allergy a) {
        return new AllergyResponse(a.getId(), a.getAllergen(), a.getReaction(), a.getCreatedAt());
    }
}
