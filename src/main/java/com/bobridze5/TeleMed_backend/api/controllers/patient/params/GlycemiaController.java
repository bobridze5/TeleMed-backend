package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.GlycemiaService;
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
@RequestMapping(API.PATIENT_ME_PARAMS_GLYCEMIA)
@RequiredArgsConstructor
public class GlycemiaController {
    private final GlycemiaService glycemiaService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    public Page<GlycemiaResponse> getGlycemiaRecords(
            @CurrentPatient Patient patient,
            @Valid @ModelAttribute GlycemiaFilterRequest request
    ) {
        return glycemiaService.getRecords(patient, request);
    }

    @GetMapping("/{id}")
    public GlycemiaResponse getGlycemiaRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        return glycemiaService.getRecordById(patient, id);
    }


    @PostMapping
    public ResponseEntity<GlycemiaResponse> addGlycemiaRecord(
            @CurrentPatient Patient patient,
            @Valid @RequestBody GlycemiaRequest request,
            HttpServletRequest servletRequest
    ) {
        GlycemiaResponse response = glycemiaService.addRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(
                servletRequest,
                API.PATIENT_ME_PARAMS_GLYCEMIA + response.id()
        );

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public GlycemiaResponse changeGlycemiaRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id,
            @Valid @RequestBody GlycemiaUpdateRequest request
    ) {
        return glycemiaService.updateRecord(patient, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGlycemiaRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ){
        glycemiaService.deleteRecord(patient, id);
    }


}
