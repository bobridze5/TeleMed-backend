package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.api.dto.review.ReviewResponse;
import com.bobridze5.TeleMed_backend.api.dto.schedule.AvailableSlotDto;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorService;
import com.bobridze5.TeleMed_backend.core.service.review.ReviewService;
import com.bobridze5.TeleMed_backend.core.service.schedule.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(API.DOCTORS)
@RequiredArgsConstructor
@Tag(name = "Врачи", description = "Получение информации о врачах")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;
    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Получить список врачей", description = "Возвращает страницу врачей с фильтрацией по специализации и городу")
    public Page<DoctorResponse> getDoctors(
            @ParameterObject @ModelAttribute DoctorFilterRequest filter,
            @PageableDefault(sort = "lastName", direction = Sort.Direction.ASC, size = 20) Pageable pageable
    ) {
        return doctorService.getDoctors(filter, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить врача по ID")
    public DoctorResponse getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @GetMapping("/{id}/slots")
    @Operation(
            summary = "Получить свободные слоты врача",
            description = "Возвращает список доступных для записи дат и времени на указанный день."
    )
    public List<AvailableSlotDto> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return scheduleService.getAvailableSlots(id, date);
    }

    @GetMapping("/{id}/reviews")
    @Operation(
            summary = "Получить отзывы о враче",
            description = "Публичный пейджинг по отзывам конкретного врача — используется на карточке " +
                    "врача в разделе «Запись к врачу» на стороне пациента."
    )
    public Page<ReviewResponse> getDoctorReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return reviewService.getDoctorReviews(id, page, size);
    }
}
