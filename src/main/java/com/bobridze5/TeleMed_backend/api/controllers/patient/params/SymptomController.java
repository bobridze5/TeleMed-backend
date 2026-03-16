package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.SymptomServicePatient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_PARAMS_SYMPTOM)
@RequiredArgsConstructor
public class SymptomController {
    private final SymptomServicePatient symptomService;

    // TODO: проблема, надо чтобы лечащий врач тоже имел доступ к данным пользователя

    @GetMapping("/{id}")
    public SymptomResponse getSymptomRecordById(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {

        return symptomService.getRecordById(patient, id);
    }

    @GetMapping
    public Page<SymptomResponse> getSymptomRecords(
            @CurrentPatient Patient patient,
            @Valid @ModelAttribute SymptomFilterRequest request
    ) {
        return symptomService.getRecords(patient, request);
    }



}
