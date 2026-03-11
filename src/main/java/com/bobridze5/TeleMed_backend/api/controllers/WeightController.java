package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.WeightService;
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
@RequestMapping(API.PARAMS_WEIGHT)
@RequiredArgsConstructor
public class WeightController {
    private final UrlBuilder urlBuilder;
    private final WeightService weightService;

    @GetMapping
    public Page<WeightResponse> getWeights(
            @Valid @ModelAttribute WeightFilterRequest request,
            @CurrentPatient Patient patient
    ) {
        return weightService.getWeightRecords(patient, request);
    }

    @GetMapping("/{weightId}")
    public WeightResponse getWeightById(
            @PathVariable("weightId") Long id,
            @CurrentPatient Patient patient
    ) {
        return weightService.getWeightRecordById(patient, id);
    }

    @PostMapping
    public ResponseEntity<WeightResponse> addWeightRecord(
            @Valid @RequestBody WeightRequest request,
            @CurrentPatient Patient patient,
            HttpServletRequest servletRequest
    ) {
        WeightResponse response = weightService.addWeightRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.USERS + response.id());
        return ResponseEntity.created(location).body(response);
    }


    @PatchMapping("/{weightId}")
    public WeightResponse changeWeight(
            @PathVariable("weightId") Long id,
            @RequestBody WeightRequest request,
            @CurrentPatient Patient patient
    ) {
        return weightService.updateWeightRecord(request, patient, id);
    }


    @DeleteMapping("/{weightId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeight(
            @PathVariable("weightId") Long id,
            @CurrentPatient Patient patient
    ) {
        weightService.deleteWeightRecord(patient, id);
    }

}
