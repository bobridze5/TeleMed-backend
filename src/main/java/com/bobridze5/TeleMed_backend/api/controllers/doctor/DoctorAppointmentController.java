package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.CancelAppointmentRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.service.appointment.AppointmentService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(API.DOCTOR_ME_APPOINTMENTS)
@RequiredArgsConstructor
@Tag(name = "Записи врача", description = "Управление записями на приём текущего врача")
public class DoctorAppointmentController {
    private final AppointmentService appointmentService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    @Operation(summary = "Получить список своих записей")
    public Page<AppointmentResponse> getAppointments(
            @CurrentDoctor Doctor doctor,
            @ParameterObject @Valid @ModelAttribute AppointmentFilterRequest filter
    ) {
        return appointmentService.getAppointments(doctor.getId(), filter);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись по ID")
    public AppointmentResponse getAppointment(
            @PathVariable Long id,
            @CurrentDoctor Doctor doctor
    ) {
        return appointmentService.getAppointmentById(id, doctor.getId());
    }

    @PostMapping
    @Operation(
            summary = "Записать пациента",
            description = "Врач самостоятельно создаёт запись к себе для указанного пациента"
    )
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @CurrentDoctor Doctor doctor,
            HttpServletRequest httpServletRequest
    ) {
        AppointmentResponse response = appointmentService.createAppointment(doctor.getId(), request);
        URI location = urlBuilder.buildAbsoluteUrl(httpServletRequest, API.DOCTOR_ME_APPOINTMENTS + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить запись")
    public AppointmentResponse updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request,
            @CurrentDoctor Doctor doctor
    ) {
        return appointmentService.updateAppointment(id, doctor.getId(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись")
    public void deleteAppointment(
            @PathVariable Long id,
            @CurrentDoctor Doctor doctor
    ) {
        appointmentService.deleteAppointment(id, doctor.getId());
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Подтвердить запись", description = "Переводит запись в статус CONFIRMED")
    public AppointmentResponse confirmAppointment(
            @PathVariable Long id,
            @CurrentDoctor Doctor doctor
    ) {
        appointmentService.confirmAppointment(id, doctor.getId(), "DOCTOR");
        return appointmentService.getAppointmentById(id, doctor.getId());
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Отменить запись", description = "Переводит запись в статус CANCELED")
    public AppointmentResponse cancelAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) CancelAppointmentRequest request,
            @CurrentDoctor Doctor doctor
    ) {
        appointmentService.cancelAppointment(id, doctor.getId(), request != null ? request.reason() : null);
        return appointmentService.getAppointmentById(id, doctor.getId());
    }
}
