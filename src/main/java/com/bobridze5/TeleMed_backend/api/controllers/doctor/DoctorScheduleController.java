package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.service.schedule.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(API.DOCTOR_ME_SCHEDULE)
@RequiredArgsConstructor
@Tag(name = "Расписание врача", description = "Управление рабочим расписанием текущего врача")
public class DoctorScheduleController {
    private final DoctorScheduleService scheduleService;

    @GetMapping
    @Operation(summary = "Получить своё расписание")
    public List<DoctorScheduleResponse> getMySchedule(@CurrentDoctor Doctor doctor) {
        return scheduleService.getSchedule(doctor.getId());
    }

    @PutMapping
    @Operation(
            summary = "Обновить расписание",
            description = "Полностью заменяет расписание врача. Передайте список дней с рабочим временем и длительностью слота."
    )
    public List<DoctorScheduleResponse> setSchedule(
            @Valid @RequestBody List<DoctorScheduleRequest> requests,
            @CurrentDoctor Doctor doctor
    ) {
        return scheduleService.setSchedule(doctor.getId(), requests);
    }
}
