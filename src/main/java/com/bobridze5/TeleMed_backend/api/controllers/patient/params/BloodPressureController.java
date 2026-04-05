package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.params.BloodPressureServicePatient;
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
@RequestMapping(API.PATIENT_ME_PARAMS_BLOOD_PRESSURE)
@RequiredArgsConstructor
@Tag(name = "Артериальное давление пациента", description = "Управление записями о давлении текущего пациента")
public class BloodPressureController {
    private final BloodPressureServicePatient bloodPressureService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    @Operation(summary = "Получить список записей давления", description = "Возвращает страницу записей артериального давления с фильтрацией по дате")
    public Page<BloodPressureResponse> getBloodPressureRecords(
            @CurrentPatient Patient patient,
            @ParameterObject @Valid @ModelAttribute BloodPressureFilterRequest request
    ) {
        return bloodPressureService.getRecords(patient, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись давления по ID")
    public BloodPressureResponse getBloodPressureRecordById(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        return bloodPressureService.getRecordById(patient, id);
    }


    @PostMapping
    @Operation(summary = "Добавить запись давления", description = "Создаёт новую запись артериального давления для текущего пациента")
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
    @Operation(summary = "Обновить запись давления")
    public BloodPressureResponse changeBloodPressureRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id,
            @Valid @RequestBody BloodPressureRequest request
    ) {
        return bloodPressureService.updateRecord(patient, id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись давления")
    public void deleteBloodPressureRecord(
            @CurrentPatient Patient patient,
            @PathVariable("id") Long id
    ) {
        bloodPressureService.deleteRecord(patient, id);
    }


}
