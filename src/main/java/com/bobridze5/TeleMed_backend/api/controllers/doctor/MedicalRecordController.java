package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordRequest;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordResponse;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalRecordUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorPatientAccessService;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_MEDICAL_RECORDS)
@RequiredArgsConstructor
@Tag(name = "Медицинские записи (врач)", description = "Создание и управление записями консультаций пациента")
public class MedicalRecordController {
    private final MedicalRecordService recordService;
    private final DoctorPatientAccessService accessService;

    @GetMapping
    @Operation(summary = "Список записей пациента")
    public Page<MedicalRecordResponse> getRecords(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return recordService.getRecordsForPatient(patient, page, size);
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "Запись по ID")
    public MedicalRecordResponse getRecord(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long recordId
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return recordService.getRecordForPatient(patient, recordId);
    }

    @PostMapping
    @Operation(summary = "Добавить запись консультации")
    public ResponseEntity<MedicalRecordResponse> createRecord(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalRecordRequest request
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        MedicalRecordResponse response = recordService.createRecord(doctor, patient, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{recordId}")
    @Operation(summary = "Обновить свою запись")
    public MedicalRecordResponse updateRecord(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long recordId,
            @Valid @RequestBody MedicalRecordUpdateRequest request
    ) {
        accessService.getPatientForDoctor(doctor, patientId);
        return recordService.updateRecord(doctor, recordId, request);
    }

    @DeleteMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить свою запись")
    public void deleteRecord(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long recordId
    ) {
        accessService.getPatientForDoctor(doctor, patientId);
        recordService.deleteRecord(doctor, recordId);
    }
}
