package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.api.dto.notification.NotificationResponse;
import com.bobridze5.TeleMed_backend.api.mappers.notification.NotificationMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.notification.Notification;
import com.bobridze5.TeleMed_backend.core.entity.notification.Reminder;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final NotificationRepository repository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null && size > 0 ? size : 20;
        Pageable pageable = PageRequest.of(p, s, Sort.by("createdAt").descending());
        return repository.findByUserId(userId, pageable)
                .map(NotificationMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = repository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Уведомление не найдено"));
        notification.setIsRead(true);
    }

    @Transactional
    public Notification create(User user, Reminder reminder, String type, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .reminder(reminder)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
        return repository.save(notification);
    }
}
