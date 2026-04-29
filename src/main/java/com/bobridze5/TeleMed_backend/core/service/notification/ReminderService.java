package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderRequest;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderResponse;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.notification.ReminderMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.notification.Reminder;
import com.bobridze5.TeleMed_backend.core.entity.notification.ReminderKind;
import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;

    @Transactional(readOnly = true)
    public Page<ReminderResponse> getReminders(Patient patient, Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null && size > 0 ? size : 20;
        Pageable pageable = PageRequest.of(p, s, Sort.by("createdAt").descending());
        return reminderRepository.findByPatientId(patient.getId(), pageable)
                .map(ReminderMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ReminderResponse getById(Patient patient, Long reminderId) {
        Reminder reminder = reminderRepository.findByIdAndPatientId(reminderId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Напоминание не найдено"));
        return ReminderMapper.mapToResponse(reminder);
    }

    @Transactional
    public ReminderResponse create(Patient patient, ReminderRequest request) {
        validateRequest(request);
        Reminder reminder = ReminderMapper.mapToEntity(request, patient);
        Reminder saved = reminderRepository.save(reminder);
        return ReminderMapper.mapToResponse(saved);
    }

    @Transactional
    public ReminderResponse update(Patient patient, Long reminderId, ReminderUpdateRequest request) {
        Reminder reminder = reminderRepository.findByIdAndPatientId(reminderId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Напоминание не найдено"));
        ReminderMapper.updateEntity(request, reminder);
        validateConsistency(reminder);
        return ReminderMapper.mapToResponse(reminder);
    }

    @Transactional
    public ReminderResponse toggle(Patient patient, Long reminderId) {
        Reminder reminder = reminderRepository.findByIdAndPatientId(reminderId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Напоминание не найдено"));
        reminder.setEnabled(!Boolean.TRUE.equals(reminder.getEnabled()));
        return ReminderMapper.mapToResponse(reminder);
    }

    @Transactional
    public void delete(Patient patient, Long reminderId) {
        Reminder reminder = reminderRepository.findByIdAndPatientId(reminderId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Напоминание не найдено"));
        reminderRepository.delete(reminder);
    }

    private void validateRequest(ReminderRequest request) {
        if (request.kind() == ReminderKind.ONE_SHOT && request.scheduledAt() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Для разового напоминания обязательна дата и время");
        }
        if (request.kind() == ReminderKind.RECURRING
                && (request.recurrenceTime() == null || request.recurrenceDays() == null || request.recurrenceDays().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Для повторяющегося напоминания обязательны время и дни недели");
        }
    }

    private void validateConsistency(Reminder reminder) {
        if (reminder.getKind() == ReminderKind.ONE_SHOT && reminder.getScheduledAt() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Для разового напоминания обязательна дата и время");
        }
        if (reminder.getKind() == ReminderKind.RECURRING
                && (reminder.getRecurrenceTime() == null || reminder.getRecurrenceDays() == null || reminder.getRecurrenceDays().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Для повторяющегося напоминания обязательны время и дни недели");
        }
    }
}
