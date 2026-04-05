package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorPatientResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API.DOCTOR_ME_PATIENTS)
@RequiredArgsConstructor
@Tag(name = "Пациенты врача", description = "Просмотр активных пациентов текущего врача")
public class DoctorPatientsController {
    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Получить список пациентов врача", description = "Возвращает страницу активных пациентов текущего врача")
    public Page<DoctorPatientResponse> getMyPatients(
            @CurrentDoctor Doctor doctor,
            @PageableDefault(sort = "assignedAt", direction = Sort.Direction.DESC, size = 20) Pageable pageable
    ) {
        return doctorService.getMyPatients(doctor, pageable);
    }
}
