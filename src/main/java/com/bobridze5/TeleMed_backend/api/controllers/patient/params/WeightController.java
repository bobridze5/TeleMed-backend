package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.WeightService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(API.PATIENT_ME_PARAMS_WEIGHT)
@RequiredArgsConstructor
@Tag(name = "Вес пациента", description = "Управление записями о весе текущего пациента")
public class WeightController {
    private final UrlBuilder urlBuilder;
    private final WeightService weightService;

    @GetMapping
    @Operation(summary = "Получить список записей о весе", description = "Возвращает страницу записей веса текущего пациента с фильтрацией по дате")
    public Page<WeightResponse> getWeights(
            @CurrentPatient Patient patient,
            @ParameterObject @Valid @ModelAttribute WeightFilterRequest request
    ) {
        return weightService.getWeightRecords(patient, request);
    }

    @GetMapping("/{weightId}")
    @Operation(summary = "Получить запись о весе по ID")
    public WeightResponse getWeightById(
            @CurrentPatient Patient patient,
            @PathVariable("weightId") Long weightId
    ) {
        return weightService.getWeightRecordById(patient, weightId);
    }

    @PostMapping
    @Operation(summary = "Добавить запись о весе", description = "Создаёт новую запись веса для текущего пациента")
    public ResponseEntity<WeightResponse> addWeightRecord(
            @CurrentPatient Patient patient,
            @Valid @RequestBody WeightRequest request,
            HttpServletRequest servletRequest
    ) {
        WeightResponse response = weightService.addWeightRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.PATIENT_ME_PARAMS_WEIGHT + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }


    @PatchMapping("/{weightId}")
    @Operation(summary = "Обновить запись о весе")
    public WeightResponse changeWeight(
            @CurrentPatient Patient patient,
            @PathVariable("weightId") Long weightId,
            @Valid @RequestBody WeightRequest request
    ) {
        return weightService.updateWeightRecord(patient, weightId, request);
    }


    @DeleteMapping("/{weightId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись о весе")
    public void deleteWeight(@CurrentPatient Patient patient, @PathVariable("weightId") Long weightId) {
        weightService.deleteWeightRecord(patient, weightId);
    }

}
