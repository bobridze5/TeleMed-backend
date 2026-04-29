package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderRequest;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderResponse;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderUpdateRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.notification.ReminderService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(API.PATIENT_ME_REMINDERS)
@RequiredArgsConstructor
@Tag(name = "Напоминания пациента", description = "Управление напоминаниями текущего пациента")
public class ReminderController {
    private final ReminderService reminderService;
    private final UrlBuilder urlBuilder;

    @GetMapping
    @Operation(summary = "Список напоминаний")
    public Page<ReminderResponse> getReminders(
            @CurrentPatient Patient patient,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return reminderService.getReminders(patient, page, size);
    }

    @GetMapping("/{reminderId}")
    @Operation(summary = "Напоминание по ID")
    public ReminderResponse getReminder(
            @CurrentPatient Patient patient,
            @PathVariable Long reminderId
    ) {
        return reminderService.getById(patient, reminderId);
    }

    @PostMapping
    @Operation(summary = "Создать напоминание")
    public ResponseEntity<ReminderResponse> create(
            @CurrentPatient Patient patient,
            @Valid @RequestBody ReminderRequest request,
            HttpServletRequest servletRequest
    ) {
        ReminderResponse response = reminderService.create(patient, request);
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.PATIENT_ME_REMINDERS + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{reminderId}")
    @Operation(summary = "Обновить напоминание")
    public ReminderResponse update(
            @CurrentPatient Patient patient,
            @PathVariable Long reminderId,
            @Valid @RequestBody ReminderUpdateRequest request
    ) {
        return reminderService.update(patient, reminderId, request);
    }

    @PostMapping("/{reminderId}/toggle")
    @Operation(summary = "Включить/выключить напоминание")
    public ReminderResponse toggle(
            @CurrentPatient Patient patient,
            @PathVariable Long reminderId
    ) {
        return reminderService.toggle(patient, reminderId);
    }

    @DeleteMapping("/{reminderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить напоминание")
    public void delete(
            @CurrentPatient Patient patient,
            @PathVariable Long reminderId
    ) {
        reminderService.delete(patient, reminderId);
    }
}
