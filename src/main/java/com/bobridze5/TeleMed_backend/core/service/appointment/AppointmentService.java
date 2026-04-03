package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.AppointmentMapper;
import com.bobridze5.TeleMed_backend.core.entity.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentStrategyFactory appointmentStrategyFactory;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper mapper;

    public AppointmentResponse getAppointmentById(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        return mapper.mapToResponse(appointment);
    }

    public Page<AppointmentResponse> getAppointments(Long userId, AppointmentFilterRequest filter) {
        PageRequest pageRequest = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by("dateTime").descending()
        );

        LocalDateTime startDate = filter.startDate();
        LocalDateTime endDate = filter.endDate();

        Page<Appointment> appointments;
        if (startDate != null && endDate != null) {
            appointments = appointmentRepository.findByPatientIdOrDoctorIdAndDateTimeBetween(
                    userId, startDate, endDate, pageRequest
            );
        } else {
            appointments = appointmentRepository.findByPatientIdOrDoctorId(userId, pageRequest);
        }

        return appointments.map(mapper::mapToResponse);
    }

    @Transactional
    public AppointmentResponse createAppointment(Long userId, AppointmentRequest request) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-инициатор не найден"));

        User target = userRepository.findById(request.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-цель не найден"));

        // TODO: Проверка времени (свободный слот)
        AppointmentCreationStrategy strategy = appointmentStrategyFactory.getStrategy(initiator);
        Appointment appointment = strategy.create(initiator, target, request);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("Создана новая запись ID: {} от пользователя ID: {} к пользователю ID: {}",
                savedAppointment.getId(), userId, request.targetId());

        return mapper.mapToResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, Long userId, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        mapper.updateEntity(request, appointment);
        Appointment updated = appointmentRepository.save(appointment);

        log.info("Обновлена запись ID: {} пользователем ID: {}", appointmentId, userId);

        return mapper.mapToResponse(updated);
    }

    @Transactional
    public void confirmAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} подтверждена пользователем ID: {}", appointmentId, userId);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointmentRepository.delete(appointment);

        log.info("Запись ID: {} удалена пользователем ID: {}", appointmentId, userId);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} отменена пользователем ID: {}", appointmentId, userId);
    }
}
