package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.CancelAppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.PatientAppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.review.ReviewRequest;
import com.bobridze5.TeleMed_backend.api.dto.review.ReviewResponse;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import com.bobridze5.TeleMed_backend.core.service.appointment.AppointmentService;
import com.bobridze5.TeleMed_backend.core.service.review.ReviewService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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
    private final ReviewService reviewService;
    private final UrlBuilder urlBuilder;

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись на приём по ID")
    public AppointmentResponse getAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return appointmentService.getAppointmentById(id, userDetails.getUserId());
    }

    @GetMapping
    @Operation(summary = "Получить список записей на приём", description = "Возвращает страницу записей пациента к врачу с фильтрацией по статусу и дате")
    public Page<AppointmentResponse> getAppointments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @ParameterObject @Valid @ModelAttribute AppointmentFilterRequest filter
    ) {
        return appointmentService.getAppointments(userDetails.getUserId(), filter);
    }

    @PostMapping
    @Operation(summary = "Создать запись к врачу", description = "Создаёт новую запись пациента к выбранному врачу на указанное время")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody PatientAppointmentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest httpServletRequest
    ) {
        AppointmentResponse response = appointmentService.createAppointment(userDetails.getUserId(), request);
        URI location = urlBuilder.buildAbsoluteUrl(httpServletRequest, API.PATIENT_ME_APPOINTMENTS + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    // Пациент не вправе менять или удалять запись: время/тип/детали встречи
    // — зона врача, а «снять» запись пациент должен через POST /{id}/cancel
    // (мягкая отмена со статусом CANCELED, чтобы сохранить историю и
    // уведомить врача).

    @PostMapping("/{id}/confirm")
    @Operation(
            summary = "Подтвердить запись на приём (со стороны пациента)",
            description = "Помечает запись подтверждённой пациентом. Запись становится CONFIRMED " +
                    "только после подтверждения обеими сторонами."
    )
    public AppointmentResponse confirmAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        appointmentService.confirmAppointment(id, userDetails.getUserId(), "PATIENT");
        return appointmentService.getAppointmentById(id, userDetails.getUserId());
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Отменить запись на приём", description = "Переводит запись в статус CANCELED")
    public AppointmentResponse cancelAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) CancelAppointmentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        appointmentService.cancelAppointment(id, userDetails.getUserId(), request != null ? request.reason() : null);
        return appointmentService.getAppointmentById(id, userDetails.getUserId());
    }

    @GetMapping("/{id}/review")
    @Operation(summary = "Получить свой отзыв об этом приёме")
    public ReviewResponse getReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return reviewService.getReviewByAppointment(id, userDetails.getUserId());
    }

    @PostMapping("/{id}/review")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Оставить отзыв", description = "Оставить отзыв после завершённой консультации")
    public ReviewResponse createReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return reviewService.createReview(id, userDetails.getUserId(), request);
    }

    @PatchMapping("/{id}/review")
    @Operation(summary = "Изменить свой отзыв", description = "Меняет рейтинг и/или комментарий ранее оставленного отзыва")
    public ReviewResponse updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return reviewService.updateReview(id, userDetails.getUserId(), request);
    }

    @DeleteMapping("/{id}/review")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить свой отзыв")
    public void deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        reviewService.deleteReview(id, userDetails.getUserId());
    }
}
