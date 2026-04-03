package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import com.bobridze5.TeleMed_backend.core.service.appointment.AppointmentService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(API.PATIENT_ME_APPOINTMENTS)
@RequiredArgsConstructor
@Tag(name = "Запись к врачу")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UrlBuilder urlBuilder;

    @GetMapping("/{id}")
    public AppointmentResponse getAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return appointmentService.getAppointmentById(id, userDetails.getUserId());
    }

    @GetMapping
    public Page<AppointmentResponse> getAppointments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @ModelAttribute AppointmentFilterRequest filter
    ) {
        return appointmentService.getAppointments(userDetails.getUserId(), filter);
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest httpServletRequest
    ) {
        AppointmentResponse response = appointmentService.createAppointment(userDetails.getUserId(), request);
        URI location = urlBuilder.buildAbsoluteUrl(httpServletRequest, API.PATIENT_ME_APPOINTMENTS + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public AppointmentResponse updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return appointmentService.updateAppointment(id, userDetails.getUserId(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        appointmentService.deleteAppointment(id, userDetails.getUserId());
    }

    @PostMapping("/{id}/confirm")
    public AppointmentResponse confirmAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        appointmentService.confirmAppointment(id, userDetails.getUserId());
        return appointmentService.getAppointmentById(id, userDetails.getUserId());
    }

    @PostMapping("/{id}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        appointmentService.cancelAppointment(id, userDetails.getUserId());
        return appointmentService.getAppointmentById(id, userDetails.getUserId());
    }
}
