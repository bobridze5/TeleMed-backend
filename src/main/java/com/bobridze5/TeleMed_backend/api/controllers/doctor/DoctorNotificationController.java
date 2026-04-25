package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorNotification;
import com.bobridze5.TeleMed_backend.core.repository.DoctorNotificationRepository;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(API.DOCTOR_ME + "/notifications")
@RequiredArgsConstructor
@Tag(name = "Уведомления врача")
@Deprecated
public class DoctorNotificationController {
    private final DoctorNotificationRepository notificationRepository;

    @GetMapping
    @Operation(summary = "Получить уведомления врача")
    public Page<DoctorNotification> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return notificationRepository.findByDoctorIdOrderByCreatedAtDesc(
                userDetails.getUserId(), PageRequest.of(page, size)
        );
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Количество непрочитанных уведомлений")
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return Map.of("count", notificationRepository.countByDoctorIdAndReadFalse(userDetails.getUserId()));
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отметить уведомление как прочитанное")
    public void markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getDoctor().getId().equals(userDetails.getUserId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
