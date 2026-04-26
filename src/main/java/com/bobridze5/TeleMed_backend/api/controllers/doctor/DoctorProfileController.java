package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.DOCTOR_ME_PROFILE)
@RequiredArgsConstructor
@Tag(name = "Профиль врача", description = "Управление профилем текущего врача")
public class DoctorProfileController {
    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Получить профиль текущего врача")
    public DoctorResponse getMyProfile(@CurrentDoctor Doctor doctor) {
        return doctorService.getMyProfile(doctor);
    }

    @PatchMapping
    @Operation(summary = "Обновить профиль текущего врача", description = "Изменяет квалификацию и базовые данные врача")
    public DoctorResponse updateMyProfile(
            @CurrentDoctor Doctor doctor,
            @Valid @RequestBody DoctorProfileUpdateRequest request
    ) {
        return doctorService.updateMyProfile(doctor, request);
    }
}
