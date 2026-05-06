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
import com.bobridze5.TeleMed_backend.core.service.notification.AppointmentNotifier;
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
    private final AppointmentNotifier appointmentNotifier;

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

        // Один запрос на medical records + один на reviews для всех записей
        // страницы — иначе AppointmentResponse.medicalRecordId/reviewId
        // вызывали бы N+1 в `mapToResponse`.
        var ids = appointments.getContent().stream().map(Appointment::getId).toList();
        var recordIdByApt = mapper.loadMedicalRecordIds(ids);
        var reviewIdByApt = mapper.loadReviewIds(ids);

        return appointments.map(a -> mapper.mapToResponse(
                a,
                recordIdByApt.get(a.getId()),
                reviewIdByApt.get(a.getId())
        ));
    }

    @Transactional
    public AppointmentResponse createAppointment(Long userId, AppointmentRequest request) {
        log.info("createAppointment START: initiator={}, targetId={}, dateTime={}, consultationType={}, slotDurationMinutes={}",
                userId, request.targetId(), request.dateTime(),
                request.consultationType(), request.slotDurationMinutes());

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-инициатор не найден"));

        User target = userRepository.findById(request.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-цель не найден"));

        AppointmentCreationStrategy strategy = appointmentStrategyFactory.getStrategy(initiator);
        Appointment appointment = strategy.create(initiator, target, request);

        log.info("createAppointment: entity built with consultationType={}", appointment.getConsultationType());

        // Две ветки валидации — в зависимости от инициатора:
        //  • Пациент: слот должен уже существовать в расписании врача и быть
        //    свободен (полная validateSlot).
        //  • Врач: слот создаём «на лету» через ensureSlotExists (с проверкой
        //    на пересечение с существующими слотами — «никаких пересечений в
        //    расписании не должно быть»), после чего достаточно убедиться, что
        //    никто другой не занял это же время.
        Long doctorIdForValidation = appointment.getDoctor().getId();
        if (initiator instanceof Doctor) {
            int duration = request.slotDurationMinutes() != null && request.slotDurationMinutes() > 0
                    ? request.slotDurationMinutes()
                    : 30;
            scheduleService.ensureSlotExists(
                    doctorIdForValidation,
                    request.dateTime(),
                    duration,
                    request.consultationType()
            );
            scheduleService.validateSlotNotTaken(doctorIdForValidation, request.dateTime());
        } else {
            scheduleService.validateSlot(doctorIdForValidation, request.dateTime(), request.consultationType());
        }

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

        // In-app уведомление обеим сторонам.
        appointmentNotifier.onCreated(savedAppointment, initiator instanceof Doctor);

        log.info("Создана новая запись ID: {} от пользователя ID: {} к пользователю ID: {}",
                savedAppointment.getId(), userId, request.targetId());

        return mapper.mapToResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, Long userId, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя изменить отменённую или завершённую запись");
        }

        // Если меняется время или тип консультации — сбрасываем оба
        // подтверждения, чтобы стороны заново согласовали новый слот.
        // Без этого получалось бы, что обе стороны «согласны» на время,
        // которое уже неактуально.
        boolean rescheduled =
                (request.dateTime() != null
                        && !request.dateTime().equals(appointment.getDateTime()))
                || (request.consultationType() != null
                        && request.consultationType() != appointment.getConsultationType());

        mapper.updateEntity(request, appointment);

        if (rescheduled) {
            appointment.setConfirmedByPatient(false);
            appointment.setConfirmedByDoctor(false);
            appointment.setConfirmedBy(null);
            appointment.setStatus(AppointmentStatus.CREATED);
            log.info("Запись ID: {} перенесена — флаги подтверждения сброшены", appointmentId);
        }

        Appointment updated = appointmentRepository.save(appointment);

        log.info("Обновлена запись ID: {} пользователем ID: {}", appointmentId, userId);

        return mapper.mapToResponse(updated);
    }

    /**
     * Двухстороннее подтверждение записи. Каждая сторона (пациент/врач)
     * подтверждает независимо; только когда обе стороны подтвердили,
     * статус переходит в CONFIRMED. До этого момента запись висит в CREATED,
     * но во фронте видно, кто уже подтвердил.
     *
     * @param confirmedByRole "PATIENT" или "DOCTOR"
     */
    @Transactional
    public void confirmAppointment(Long appointmentId, Long userId, String confirmedByRole) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя подтвердить отменённую или завершённую запись");
        }

        if ("PATIENT".equals(confirmedByRole)) {
            appointment.setConfirmedByPatient(true);
        } else if ("DOCTOR".equals(confirmedByRole)) {
            appointment.setConfirmedByDoctor(true);
        } else {
            throw new IllegalArgumentException("Неизвестная роль подтверждения: " + confirmedByRole);
        }

        // Старое поле — пишем сюда последнего подтвердившего, чтобы не сломать
        // потенциальных потребителей.
        appointment.setConfirmedBy(confirmedByRole);

        // Запись считается подтверждённой только когда обе стороны нажали «подтвердить».
        if (Boolean.TRUE.equals(appointment.getConfirmedByPatient())
                && Boolean.TRUE.equals(appointment.getConfirmedByDoctor())) {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
        }

        appointmentRepository.save(appointment);

        appointmentNotifier.onConfirmed(appointment, confirmedByRole);

        log.info("Запись ID: {} подтверждена ролью {}; pat={}, doc={}, status={}",
                appointmentId, confirmedByRole,
                appointment.getConfirmedByPatient(),
                appointment.getConfirmedByDoctor(),
                appointment.getStatus());
    }

    /**
     * Подтверждение записи врачом с заполнением деталей встречи
     * (ссылка/телефон/заметки и пароль). Поля опциональны и обновляются
     * только если переданы непустые значения.
     *
     * Вся логика в одном методе и одной транзакции, чтобы:
     *  1) не делать лишний SQL-select (был дубль с self-invocation);
     *  2) гарантировать атомарность «детали + подтверждение».
     */
    @Transactional
    public void confirmAppointmentByDoctor(Long appointmentId, Long doctorId,
                                           String meetingLink, String meetingPhone, String meetingNotes) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя подтвердить отменённую или завершённую запись");
        }

        if (meetingLink != null) {
            appointment.setMeetingLink(meetingLink.isBlank() ? null : meetingLink.trim());
        }
        if (meetingPhone != null) {
            appointment.setMeetingPhone(meetingPhone.isBlank() ? null : meetingPhone.trim());
        }
        if (meetingNotes != null) {
            appointment.setMeetingNotes(meetingNotes.isBlank() ? null : meetingNotes.trim());
        }

        appointment.setConfirmedByDoctor(true);
        appointment.setConfirmedBy("DOCTOR");

        if (Boolean.TRUE.equals(appointment.getConfirmedByPatient())
                && Boolean.TRUE.equals(appointment.getConfirmedByDoctor())) {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
        }

        appointmentRepository.save(appointment);

        appointmentNotifier.onConfirmed(appointment, "DOCTOR");

        log.info("Запись ID: {} подтверждена врачом ID: {}; pat={}, doc={}, status={}",
                appointmentId, doctorId,
                appointment.getConfirmedByPatient(),
                appointment.getConfirmedByDoctor(),
                appointment.getStatus());
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

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Запись уже завершена или отменена");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        if (reason != null && !reason.isBlank()) {
            appointment.setReason(reason);
        }
        appointmentRepository.save(appointment);

        // Узнаём, кто отменил, по совпадению userId с patient/doctor — иначе
        // у фронта нет надёжного способа сказать «другой стороне», что приём
        // отменили.
        String cancelledBy = appointment.getPatient().getId().equals(userId) ? "PATIENT" : "DOCTOR";
        appointmentNotifier.onCanceled(appointment, cancelledBy, reason);

        log.info("Запись ID: {} отменена пользователем ID: {}", appointmentId, userId);
    }

    /**
     * Отметка «Пациент не явился» — отдельная от обычной отмены.
     * Используется только врачом и переводит запись в статус NO_SHOW.
     * Доступно только из CONFIRMED (нелогично «не являться» туда, что
     * стороны и не подтверждали).
     */
    @Transactional
    public void markNoShow(Long appointmentId, Long doctorId, String reason) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Отметить «Не явился» можно только подтверждённую запись " +
                    "(текущий статус: " + appointment.getStatus() + ")"
            );
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        if (reason != null && !reason.isBlank()) {
            appointment.setReason(reason);
        }
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} отмечена как NO_SHOW врачом ID: {}", appointmentId, doctorId);
    }

    @Transactional
    public void completeAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        // Завершить можно только подтверждённую запись.
        // Из CREATED завершать нельзя — иначе можно «провести» консультацию,
        // которая ещё не была согласована обеими сторонами.
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Завершить можно только подтверждённую обеими сторонами запись " +
                    "(текущий статус: " + appointment.getStatus() + ")"
            );
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        log.info("Запись ID: {} завершена пользователем ID: {}", appointmentId, userId);
    }
}
