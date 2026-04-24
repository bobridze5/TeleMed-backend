package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.AllergyRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.AllergyResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.medcard.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_ALLERGIES)
@RequiredArgsConstructor
@Tag(name = "Аллергии пациента")
public class AllergyController {
    private final AllergyService allergyService;

    @GetMapping
    @Operation(summary = "Список аллергий")
    public Page<AllergyResponse> getAll(
            @CurrentPatient Patient patient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return allergyService.getAll(patient, page, size);
    }

    @PostMapping
    @Operation(summary = "Добавить аллергию")
    public ResponseEntity<AllergyResponse> create(
            @CurrentPatient Patient patient,
            @Valid @RequestBody AllergyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(allergyService.create(patient, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить аллергию")
    public void delete(@CurrentPatient Patient patient, @PathVariable Long id) {
        allergyService.delete(patient, id);
    }
}
