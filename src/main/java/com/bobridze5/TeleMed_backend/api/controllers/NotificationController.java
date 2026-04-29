package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.notification.NotificationResponse;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import com.bobridze5.TeleMed_backend.core.service.notification.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(API.NOTIFICATIONS_ME)
@RequiredArgsConstructor
@Tag(name = "Уведомления", description = "Лента уведомлений текущего пользователя (любой роли)")
public class NotificationController {
    private final UserNotificationService notificationService;

    @GetMapping
    @Operation(summary = "Список уведомлений")
    public Page<NotificationResponse> list(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return notificationService.getNotifications(userDetails.getUserId(), page, size);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Количество непрочитанных")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return Map.of("count", notificationService.getUnreadCount(userDetails.getUserId()));
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Пометить как прочитанное")
    public void markRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId
    ) {
        notificationService.markRead(userDetails.getUserId(), notificationId);
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить уведомление")
    public void delete(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId
    ) {
        notificationService.delete(userDetails.getUserId(), notificationId);
    }
}
