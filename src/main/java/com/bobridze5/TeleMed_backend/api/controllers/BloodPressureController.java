package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.BloodPressureService;
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
@RequestMapping(API.PARAMS_BLOOD_PRESSURE)
@RequiredArgsConstructor
public class BloodPressureController {
    private final BloodPressureService bloodPressureService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    public Page<BloodPressureResponse> getBloodPressureRecords(
            @Valid @ModelAttribute BloodPressureFilterRequest request,
            @CurrentPatient Patient patient
    ) {
        return bloodPressureService.getRecords(patient, request);
    }

    @GetMapping("/{id}")
    public BloodPressureResponse getBloodPressureRecordById(
            @PathVariable("id") Long id,
            @CurrentPatient Patient patient
    ) {
        return bloodPressureService.getRecordById(patient, id);
    }


    @PostMapping
    public ResponseEntity<BloodPressureResponse> addBloodPressureRecord(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody BloodPressureRequest request,
            @CurrentPatient Patient patient
    ) {
        BloodPressureResponse response = bloodPressureService.addRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(httpServletRequest, API.PARAMS_BLOOD_PRESSURE);

        return ResponseEntity.created(location).body(response);
    }


    @PatchMapping("/{id}")
    public BloodPressureResponse changeWeight(
            @PathVariable("id") Long id,
            @Valid @RequestBody BloodPressureRequest request,
            @CurrentPatient Patient patient
    ) {
        return bloodPressureService.updateRecord(patient, id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeight(
            @PathVariable("id") Long id,
            @CurrentPatient Patient patient
    ) {
        bloodPressureService.deleteRecord(patient, id);
    }


}
