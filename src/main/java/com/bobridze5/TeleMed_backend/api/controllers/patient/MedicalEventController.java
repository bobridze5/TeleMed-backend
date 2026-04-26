package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalEventRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalEventResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicalEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_MEDICAL_EVENTS)
@RequiredArgsConstructor
@Tag(name = "История болезней пациента")
public class MedicalEventController {
    private final MedicalEventService medicalEventService;

    @GetMapping
    @Operation(summary = "Список медицинских событий")
    public Page<MedicalEventResponse> getAll(
            @CurrentPatient Patient patient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return medicalEventService.getAll(patient, page, size);
    }

    @PostMapping
    @Operation(summary = "Добавить медицинское событие")
    public ResponseEntity<MedicalEventResponse> create(
            @CurrentPatient Patient patient,
            @Valid @RequestBody MedicalEventRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalEventService.create(patient, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить медицинское событие")
    public void delete(@CurrentPatient Patient patient, @PathVariable Long id) {
        medicalEventService.delete(patient, id);
    }
}
