package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.SymptomServicePatient;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(API.PATIENT_ME_PARAMS_SYMPTOM)
@RequiredArgsConstructor
public class SymptomController {
    private final SymptomServicePatient symptomService;
    private final UrlBuilder urlBuilder;

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

    @PostMapping
    public ResponseEntity<SymptomResponse> addSymptomRecord(
            @CurrentPatient Patient patient,
            @Valid @RequestBody SymptomRequest request,
            HttpServletRequest servletRequest
    ) {
        SymptomResponse response = symptomService.addRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.PATIENT_ME_PARAMS_SYMPTOM + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public SymptomResponse updateSymptomRecord(
            @CurrentPatient Patient patient,
            @PathVariable Long id,
            @Valid @RequestBody SymptomUpdateRequest request
    ) {
        return symptomService.updateRecord(patient, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSymptomRecord(
            @CurrentPatient Patient patient,
            @PathVariable Long id
    ) {
        symptomService.deleteRecord(patient, id);
    }


}
