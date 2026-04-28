package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.params.InsulinMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinDose;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.InsulinRepository;
import com.bobridze5.TeleMed_backend.core.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InsulinDoseService {
    private final InsulinRepository insulinRepository;
    private final MealRepository mealRepository;

    @Transactional(readOnly = true)
    public Page<InsulinDoseResponse> getDoses(Patient patient, InsulinDoseFilterRequest request) {
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null && request.size() > 0 ? request.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("takenAt").descending());

        Long patientId = patient.getId();

        Page<InsulinDose> dosePage = request.isBetween()
                ? insulinRepository.findByPatientIdAndTakenAtBetween(
                        patientId, request.startDate(), request.endDate(), pageable)
                : insulinRepository.findByPatientId(patientId, pageable);

        return dosePage.map(InsulinMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public InsulinDoseResponse getDoseById(Patient patient, Long doseId) {
        InsulinDose dose = insulinRepository.findByIdAndPatientId(doseId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись инсулина не найдена"));

        return InsulinMapper.mapToResponse(dose);
    }

    @Transactional
    public InsulinDoseResponse addDose(Patient patient, InsulinDoseRequest request) {
        Meal meal = resolveMeal(patient, request.mealId());
        InsulinDose insulinDose = InsulinMapper.mapToEntity(request, patient, meal);

        InsulinDose savedInsulinDose = insulinRepository.save(insulinDose);

        return InsulinMapper.mapToResponse(savedInsulinDose);
    }

    @Transactional
    public InsulinDoseResponse updateDose(Patient patient, Long doseId, InsulinDoseUpdateRequest request) {
        InsulinDose dose = insulinRepository.findByIdAndPatientId(doseId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись инсулина не найдена"));

        Meal meal = request.mealId() != null
                ? resolveMeal(patient, request.mealId())
                : dose.getMeal();

        InsulinMapper.updateEntity(request, dose, meal);

        return InsulinMapper.mapToResponse(dose);
    }

    @Transactional
    public void deleteDose(Patient patient, Long doseId) {
        InsulinDose dose = insulinRepository.findByIdAndPatientId(doseId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Запись инсулина не найдена"));

        insulinRepository.delete(dose);
    }

    private Meal resolveMeal(Patient patient, Long mealId) {
        if (mealId == null) return null;
        return mealRepository.findByIdAndPatientId(mealId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Приём пищи не найден"));
    }
}
