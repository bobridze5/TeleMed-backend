package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.PhysicalActivityService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(API.PATIENT_ME_PARAMS_PHYSICAL_ACTIVITY)
@RequiredArgsConstructor
@Tag(name = "Физическая активность пациента", description = "Управление записями о физической активности текущего пациента")
public class PhysicalActivityController {
    private final PhysicalActivityService physicalActivityService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    @Operation(summary = "Получить список записей активности", description = "Возвращает страницу записей физической активности с фильтрацией по дате")
    public Page<PhysicalActivityResponse> getPhysicalActivityRecords(
            @CurrentPatient Patient patient,
            @Valid @ModelAttribute PhysicalActivityFilterRequest request
    ) {
        return physicalActivityService.getRecords(patient, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись активности по ID")
    public PhysicalActivityResponse getPhysicalActivityRecordById(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        return physicalActivityService.getRecordById(patient, id);
    }

    @PostMapping
    @Operation(summary = "Добавить запись активности", description = "Создаёт новую запись физической активности для текущего пациента")
    public ResponseEntity<PhysicalActivityResponse> addPhysicalActivityRecord(
            @CurrentPatient Patient patient,
            @Valid @RequestBody PhysicalActivityRequest request,
            HttpServletRequest servletRequest
    ) {
        PhysicalActivityResponse response = physicalActivityService.addRecord(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(
                servletRequest,
                API.PATIENT_ME_PARAMS_PHYSICAL_ACTIVITY + response.id()
        );
        return ResponseEntity.created(location).body(response);
    }


    @PatchMapping("/{id}")
    @Operation(summary = "Обновить запись активности")
    public PhysicalActivityResponse changePhysicalActivityRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id,
            @Valid @RequestBody PhysicalActivityUpdateRequest request
    ) {
        return physicalActivityService.updateRecord(patient, id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись активности")
    public void deleteWeight(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        physicalActivityService.deleteRecord(patient, id);
    }
}
