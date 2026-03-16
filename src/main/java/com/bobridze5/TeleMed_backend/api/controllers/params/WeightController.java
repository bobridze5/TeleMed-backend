package com.bobridze5.TeleMed_backend.api.controllers.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
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
@RequestMapping(API.PATIENT_OWNER_PARAMS_WEIGHT)
@RequiredArgsConstructor
public class WeightController {
    private final UrlBuilder urlBuilder;
    private final WeightService weightService;

    @GetMapping
    public Page<WeightResponse> getWeights(
            @CurrentPatient Patient patient,
            @Valid @ModelAttribute WeightFilterRequest request
    ) {
        return weightService.getWeightRecords(patient, request);
    }

    @GetMapping("/{weightId}")
    public WeightResponse getWeightById(
            @CurrentPatient Patient patient,
            @PathVariable("weightId") Long weightId
    ) {
        return weightService.getWeightRecordById(patient, weightId);
    }

    @PostMapping
    public ResponseEntity<WeightResponse> addWeightRecord(
            @CurrentPatient Patient patient,
            @Valid @RequestBody WeightRequest request,
            HttpServletRequest servletRequest
    ) {
        WeightResponse response = weightService.addWeightRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.USERS + response.id());
        return ResponseEntity.created(location).body(response);
    }


    @PatchMapping("/{weightId}")
    public WeightResponse changeWeight(
            @CurrentPatient Patient patient,
            @PathVariable("weightId") Long weightId,
            @RequestBody WeightRequest request
    ) {
        return weightService.updateWeightRecord(patient, weightId, request);
    }


    @DeleteMapping("/{weightId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeight(
            @CurrentPatient Patient patient,
            @PathVariable("weightId") Long weightId
    ) {
        weightService.deleteWeightRecord(patient, weightId);
    }

}
