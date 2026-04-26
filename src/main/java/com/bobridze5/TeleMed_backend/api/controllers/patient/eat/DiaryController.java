package com.bobridze5.TeleMed_backend.api.controllers.patient.eat;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.diary.NutritionDayResponse;
import com.bobridze5.TeleMed_backend.api.dto.diary.NutritionDayUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.meal.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(API.PATIENT_ME_DIARY)
@RequiredArgsConstructor
@Tag(name = "Дневник питания (пациент)", description = "Агрегированный просмотр питания по дням")
public class DiaryController {
    private final DiaryService diaryService;

    @GetMapping("/{date}")
    @Operation(summary = "Дневник за день")
    public NutritionDayResponse getDay(
            @CurrentPatient Patient patient,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return diaryService.getDay(patient, date);
    }

    @GetMapping
    @Operation(summary = "Дневник за период", description = "Параметры from и to обязательны (ISO даты)")
    public List<NutritionDayResponse> getRange(
            @CurrentPatient Patient patient,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return diaryService.getRange(patient, from, to);
    }

    @PatchMapping("/{date}")
    @Operation(summary = "Обновить заметки/статус дня")
    public NutritionDayResponse updateDay(
            @CurrentPatient Patient patient,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody NutritionDayUpdateRequest request
    ) {
        return diaryService.updateDay(patient, date, request);
    }
}
