package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.api.dto.medcard.AllergyResponse;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalEventResponse;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicationResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure.BloodPressureResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.glycemia.GlycemiaResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.physical_activity.PhysicalActivityResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.symptom.SymptomResponse;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.params.weight.WeightResponse;
import com.bobridze5.TeleMed_backend.api.dto.profile.PatientProfileResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorPatientAccessService;
import com.bobridze5.TeleMed_backend.core.service.meal.MealService;
import com.bobridze5.TeleMed_backend.core.service.medcard.AllergyService;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicalEventService;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicationService;
import com.bobridze5.TeleMed_backend.core.service.params.*;
import com.bobridze5.TeleMed_backend.core.service.profile.PatientProfileService;
import com.bobridze5.TeleMed_backend.core.service.report.PatientReportService;
import com.bobridze5.TeleMed_backend.core.service.report.ReportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(API.PATIENT_ID)
@RequiredArgsConstructor
@Tag(name = "Дневник пациента (врач)", description = "Просмотр данных дневника самоконтроля прикреплённого пациента")
public class DoctorPatientParamsController {
    private final DoctorPatientAccessService accessService;
    private final WeightService weightService;
    private final BloodPressureServicePatient bloodPressureService;
    private final GlycemiaService glycemiaService;
    private final SymptomServicePatient symptomService;
    private final PhysicalActivityService physicalActivityService;
    private final PatientReportService reportService;
    private final MealService mealService;
    private final PatientProfileService patientProfileService;
    private final AllergyService allergyService;
    private final MedicationService medicationService;
    private final MedicalEventService medicalEventService;

    @GetMapping("/weights")
    @Operation(summary = "Вес пациента — список")
    public Page<WeightResponse> getWeights(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @ParameterObject @Valid @ModelAttribute WeightFilterRequest filter
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return weightService.getWeightRecords(patient, filter);
    }

    @GetMapping("/weights/{id}")
    @Operation(summary = "Вес пациента — по ID")
    public WeightResponse getWeightById(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long id
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return weightService.getWeightRecordById(patient, id);
    }

    @GetMapping("/blood-pressures")
    @Operation(summary = "Давление пациента — список")
    public Page<BloodPressureResponse> getBloodPressures(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @ParameterObject @Valid @ModelAttribute BloodPressureFilterRequest filter
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return bloodPressureService.getRecords(patient, filter);
    }

    @GetMapping("/blood-pressures/{id}")
    @Operation(summary = "Давление пациента — по ID")
    public BloodPressureResponse getBloodPressureById(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long id
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return bloodPressureService.getRecordById(patient, id);
    }

    @GetMapping("/glycemia")
    @Operation(summary = "Гликемия пациента — список")
    public Page<GlycemiaResponse> getGlycemia(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @ParameterObject @Valid @ModelAttribute GlycemiaFilterRequest filter
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return glycemiaService.getRecords(patient, filter);
    }

    @GetMapping("/glycemia/{id}")
    @Operation(summary = "Гликемия пациента — по ID")
    public GlycemiaResponse getGlycemiaById(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long id
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return glycemiaService.getRecordById(patient, id);
    }

    @GetMapping("/symptoms")
    @Operation(summary = "Симптомы пациента — список")
    public Page<SymptomResponse> getSymptoms(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @ParameterObject @Valid @ModelAttribute SymptomFilterRequest filter
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return symptomService.getRecords(patient, filter);
    }

    @GetMapping("/symptoms/{id}")
    @Operation(summary = "Симптомы пациента — по ID")
    public SymptomResponse getSymptomById(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long id
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return symptomService.getRecordById(patient, id);
    }

    @GetMapping("/physical-activities")
    @Operation(summary = "Физическая активность пациента — список")
    public Page<PhysicalActivityResponse> getPhysicalActivities(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @ParameterObject @Valid @ModelAttribute PhysicalActivityFilterRequest filter
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return physicalActivityService.getRecords(patient, filter);
    }

    @GetMapping("/physical-activities/{id}")
    @Operation(summary = "Физическая активность пациента — по ID")
    public PhysicalActivityResponse getPhysicalActivityById(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long id
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return physicalActivityService.getRecordById(patient, id);
    }

    @GetMapping("/profile")
    @Operation(summary = "Профиль пациента")
    public PatientProfileResponse getProfile(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return patientProfileService.getProfile(patient);
    }

    @GetMapping("/meals")
    @Operation(summary = "Приёмы пищи пациента — список")
    public Page<MealResponse> getMeals(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @ParameterObject @ModelAttribute MealFilterRequest filter,
            Pageable pageable
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return mealService.getMeals(patient, filter, pageable);
    }

    @GetMapping("/meals/{mealId}")
    @Operation(summary = "Приём пищи пациента — по ID")
    public MealResponse getMealById(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable Long mealId
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return mealService.getMealById(patient, mealId);
    }

    @GetMapping("/allergies")
    @Operation(summary = "Аллергии пациента — список")
    public Page<AllergyResponse> getAllergies(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return allergyService.getAll(patient, page, size);
    }

    @GetMapping("/medications")
    @Operation(summary = "Препараты пациента — список")
    public Page<MedicationResponse> getMedications(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return medicationService.getAll(patient, page, size);
    }

    @GetMapping("/medical-events")
    @Operation(summary = "История болезней пациента — список")
    public Page<MedicalEventResponse> getMedicalEvents(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return medicalEventService.getAll(patient, page, size);
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "PDF-отчёт пациента", description = "type: GENERAL | WEIGHT | GLYCEMIA | BLOOD_PRESSURE")
    public ResponseEntity<byte[]> getPatientReport(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "GENERAL") ReportType type
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        byte[] pdf = reportService.generateReport(patient, from, to, type);
        String filename = "report_" + type.name().toLowerCase() + "_patient_" + patientId + "_" + from + "_" + to + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
