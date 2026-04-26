package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.schedule.AutoGenerateRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.CreateOrUpdateSlotRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.api.dto.schedule.SlotResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.service.schedule.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping(API.DOCTOR_ME_SCHEDULE)
@RequiredArgsConstructor
@Tag(name = "Расписание врача", description = "Управление рабочим расписанием текущего врача")
public class DoctorScheduleController {
    private final DoctorScheduleService scheduleService;

    @GetMapping
    @Operation(summary = "Получить своё расписание (все дни)")
    public List<DoctorScheduleResponse> getMySchedule(@CurrentDoctor Doctor doctor) {
        return scheduleService.getSchedule(doctor.getId());
    }

    @PutMapping
    @Operation(
            summary = "Полностью заменить расписание",
            description = "Передайте список дней с customSlots. Слоты внутри одного дня не должны пересекаться."
    )
    public List<DoctorScheduleResponse> setSchedule(
            @Valid @RequestBody List<DoctorScheduleRequest> requests,
            @CurrentDoctor Doctor doctor
    ) {
        return scheduleService.setSchedule(doctor.getId(), requests);
    }

    @GetMapping("/days/{dayOfWeek}")
    @Operation(summary = "Получить расписание для дня недели")
    public DoctorScheduleResponse getScheduleByDayOfWeek(
            @CurrentDoctor Doctor doctor,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        return scheduleService.getScheduleByDayOfWeek(doctor.getId(), dayOfWeek);
    }

    @PostMapping("/days/{dayOfWeek}/slots")
    @Operation(summary = "Добавить слот", description = "Слот не должен пересекаться с существующими.")
    public ResponseEntity<SlotResponse> addSlot(
            @CurrentDoctor Doctor doctor,
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody CreateOrUpdateSlotRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.addSlot(doctor.getId(), dayOfWeek, request));
    }

    @PutMapping("/slots/{slotId}")
    @Operation(summary = "Обновить слот", description = "Не должен пересекаться с другими слотами того же дня.")
    public SlotResponse updateSlot(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long slotId,
            @Valid @RequestBody CreateOrUpdateSlotRequest request
    ) {
        return scheduleService.updateSlot(doctor.getId(), slotId, request);
    }

    @DeleteMapping("/slots/{slotId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить слот")
    public void deleteSlot(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long slotId
    ) {
        scheduleService.deleteSlot(doctor.getId(), slotId);
    }

    @PostMapping("/days/{dayOfWeek}/auto-generate")
    @Operation(
            summary = "Авто-генерация слотов",
            description = "Создаёт слоты от fromTime до toTime с шагом slotMinutes. " +
                    "Кандидаты, пересекающиеся с уже существующими customSlots дня, пропускаются."
    )
    public DoctorScheduleResponse autoGenerate(
            @CurrentDoctor Doctor doctor,
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody AutoGenerateRequest request
    ) {
        return scheduleService.autoGenerate(doctor.getId(), dayOfWeek, request);
    }
}
