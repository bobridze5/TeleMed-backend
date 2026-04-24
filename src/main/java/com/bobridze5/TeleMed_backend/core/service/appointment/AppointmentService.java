package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.AppointmentMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.medical.PatientDoctorAssignment;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientDoctorAssignmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import com.bobridze5.TeleMed_backend.core.service.schedule.DoctorScheduleService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentStrategyFactory appointmentStrategyFactory;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final AppointmentMapper mapper;
    private final DoctorScheduleService scheduleService;

    public AppointmentResponse getAppointmentById(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        return mapper.mapToResponse(appointment);
    }

    public Page<AppointmentResponse> getAppointments(Long userId, AppointmentFilterRequest filter) {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null && filter.size() > 0 ? filter.size() : 20;
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by("dateTime").descending()
        );

        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.or(
                    cb.equal(root.get("patient").get("id"), userId),
                    cb.equal(root.get("doctor").get("id"), userId)
            ));
            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), filter.startDate()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), filter.endDate()));
            }
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Appointment> appointments = appointmentRepository.findAll(spec, pageRequest);
        return appointments.map(mapper::mapToResponse);
    }

    @Transactional
    public AppointmentResponse createAppointment(Long userId, AppointmentRequest request) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-инициатор не найден"));

        User target = userRepository.findById(request.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-цель не найден"));

        AppointmentCreationStrategy strategy = appointmentStrategyFactory.getStrategy(initiator);
        Appointment appointment = strategy.create(initiator, target, request);

        scheduleService.validateSlot(appointment.getDoctor().getId(), request.dateTime(), request.consultationType());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Автоматически создаём привязку пациент-врач, если её ещё нет
        if (initiator instanceof Patient patient && target instanceof Doctor doctor) {
            if (!assignmentRepository.existsAssignment(doctor.getId(), patient.getId())) {
                assignmentRepository.save(PatientDoctorAssignment.builder()
                        .patient(patient)
                        .doctor(doctor)
                        .active(true)
                        .build());
                log.info("Создана привязка пациент ID:{} к врачу ID:{}", patient.getId(), doctor.getId());
            }
        }

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
    public void confirmAppointment(Long appointmentId, Long userId, String confirmedByRole) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedBy(confirmedByRole);
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} подтверждена пользователем ID: {} (роль: {})", appointmentId, userId, confirmedByRole);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointmentRepository.delete(appointment);

        log.info("Запись ID: {} удалена пользователем ID: {}", appointmentId, userId);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId, String reason) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointment.setStatus(AppointmentStatus.CANCELED);
        if (reason != null && !reason.isBlank()) {
            appointment.setReason(reason);
        }
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} отменена пользователем ID: {}", appointmentId, userId);
    }

    @Transactional
    public void completeAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} завершена пользователем ID: {}", appointmentId, userId);
    }
}
