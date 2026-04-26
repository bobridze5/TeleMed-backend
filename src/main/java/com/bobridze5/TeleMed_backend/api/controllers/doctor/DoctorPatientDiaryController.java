package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.diary.NutritionDayResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorPatientAccessService;
import com.bobridze5.TeleMed_backend.core.service.meal.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(API.DOCTOR_ME_PATIENT_DIARY)
@RequiredArgsConstructor
@Tag(name = "Дневник питания пациента (врач)", description = "Просмотр питания прикреплённого пациента")
public class DoctorPatientDiaryController {
    private final DiaryService diaryService;
    private final DoctorPatientAccessService accessService;

    @GetMapping("/{date}")
    @Operation(summary = "Дневник пациента за день")
    public NutritionDayResponse getDay(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return diaryService.getDay(patient, date);
    }

    @GetMapping
    @Operation(summary = "Дневник пациента за период", description = "Параметры from и to обязательны (ISO даты)")
    public List<NutritionDayResponse> getRange(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return diaryService.getRange(patient, from, to);
    }
}
