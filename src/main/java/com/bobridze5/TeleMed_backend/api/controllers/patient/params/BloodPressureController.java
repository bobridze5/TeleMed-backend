package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.BloodPressureServicePatient;
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
@RequestMapping(API.PATIENT_OWNER_PARAMS_BLOOD_PRESSURE)
@RequiredArgsConstructor
public class BloodPressureController {
    private final BloodPressureServicePatient bloodPressureService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    public Page<BloodPressureResponse> getBloodPressureRecords(
            @CurrentPatient Patient patient,
            @Valid @ModelAttribute BloodPressureFilterRequest request
    ) {
        return bloodPressureService.getRecords(patient, request);
    }

    @GetMapping("/{id}")
    public BloodPressureResponse getBloodPressureRecordById(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        return bloodPressureService.getRecordById(patient, id);
    }


    @PostMapping
    public ResponseEntity<BloodPressureResponse> addBloodPressureRecord(
            @CurrentPatient Patient patient,
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody BloodPressureRequest request
    ) {
        BloodPressureResponse response = bloodPressureService.addRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(httpServletRequest, API.PATIENT_PARAMS_BLOOD_PRESSURE);

        return ResponseEntity.created(location).body(response);
    }


    @PatchMapping("/{id}")
    public BloodPressureResponse changeBloodPressureRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id,
            @Valid @RequestBody BloodPressureRequest request
    ) {
        return bloodPressureService.updateRecord(patient, id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBloodPressureRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        bloodPressureService.deleteRecord(patient, id);
    }


}
