package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_MEDICAL_RECORDS)
@RequiredArgsConstructor
@Tag(name = "Медицинские записи (пациент)", description = "Просмотр своих записей консультаций")
public class PatientMedicalRecordController {
    private final MedicalRecordService recordService;

    @GetMapping
    @Operation(summary = "Мои записи консультаций")
    public Page<MedicalRecordResponse> getMyRecords(
            @CurrentPatient Patient patient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return recordService.getRecordsForPatient(patient, page, size);
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "Моя запись консультации по ID")
    public MedicalRecordResponse getMyRecord(
            @CurrentPatient Patient patient,
            @PathVariable Long recordId
    ) {
        return recordService.getRecordForPatient(patient, recordId);
    }
}
