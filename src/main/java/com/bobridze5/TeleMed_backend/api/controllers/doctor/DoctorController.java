package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.DOCTORS)
@RequiredArgsConstructor
@Tag(name = "Врачи", description = "Получение информации о врачах")
public class DoctorController {
    private final DoctorService doctorService;

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
}
