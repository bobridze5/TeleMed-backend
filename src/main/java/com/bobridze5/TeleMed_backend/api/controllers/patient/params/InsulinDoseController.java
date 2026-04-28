package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.insulin.InsulinDoseUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.InsulinDoseService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(API.PATIENT_ME_PARAMS_INSULIN)
@RequiredArgsConstructor
@Tag(name = "Дозы инсулина пациента", description = "Управление записями о дозах инсулина текущего пациента")
public class InsulinDoseController {
    private final UrlBuilder urlBuilder;
    private final InsulinDoseService insulinDoseService;

    @GetMapping
    @Operation(summary = "Получить список доз инсулина", description = "Возвращает страницу записей с фильтрацией по дате, типу и количеству единиц")
    public Page<InsulinDoseResponse> getDoses(
            @CurrentPatient Patient patient,
            @ParameterObject @Valid @ModelAttribute InsulinDoseFilterRequest request
    ) {
        return insulinDoseService.getDoses(patient, request);
    }

    @GetMapping("/{doseId}")
    @Operation(summary = "Получить запись о дозе инсулина по ID")
    public InsulinDoseResponse getDoseById(
            @CurrentPatient Patient patient,
            @PathVariable("doseId") Long doseId
    ) {
        return insulinDoseService.getDoseById(patient, doseId);
    }

    @PostMapping
    @Operation(summary = "Добавить запись о дозе инсулина")
    public ResponseEntity<InsulinDoseResponse> addDose(
            @CurrentPatient Patient patient,
            @Valid @RequestBody InsulinDoseRequest request,
            HttpServletRequest servletRequest
    ) {
        InsulinDoseResponse response = insulinDoseService.addDose(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.PATIENT_ME_PARAMS_INSULIN + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{doseId}")
    @Operation(summary = "Обновить запись о дозе инсулина")
    public InsulinDoseResponse updateDose(
            @CurrentPatient Patient patient,
            @PathVariable("doseId") Long doseId,
            @Valid @RequestBody InsulinDoseUpdateRequest request
    ) {
        return insulinDoseService.updateDose(patient, doseId, request);
    }

    @DeleteMapping("/{doseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись о дозе инсулина")
    public void deleteDose(
            @CurrentPatient Patient patient,
            @PathVariable("doseId") Long doseId
    ) {
        insulinDoseService.deleteDose(patient, doseId);
    }
}
