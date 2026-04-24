package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicationRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicationResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_MEDICATIONS)
@RequiredArgsConstructor
@Tag(name = "Препараты пациента")
public class MedicationController {
    private final MedicationService medicationService;

    @GetMapping
    @Operation(summary = "Список препаратов")
    public Page<MedicationResponse> getAll(
            @CurrentPatient Patient patient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return medicationService.getAll(patient, page, size);
    }

    @PostMapping
    @Operation(summary = "Добавить препарат")
    public ResponseEntity<MedicationResponse> create(
            @CurrentPatient Patient patient,
            @Valid @RequestBody MedicationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.create(patient, request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить препарат")
    public MedicationResponse update(
            @CurrentPatient Patient patient,
            @PathVariable Long id,
            @Valid @RequestBody MedicationRequest request
    ) {
        return medicationService.update(patient, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить препарат")
    public void delete(@CurrentPatient Patient patient, @PathVariable Long id) {
        medicationService.delete(patient, id);
    }
}
