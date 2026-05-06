package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentCreationRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.api.mappers.AppointmentMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.exceptions.AccessForbiddenException;
import com.bobridze5.TeleMed_backend.core.exceptions.AppointmentStateException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import com.bobridze5.TeleMed_backend.core.service.doctor.AssignmentService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentStrategyFactory appointmentStrategyFactory;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AssignmentService assignmentService;
    private final AppointmentMapper mapper;
    private final AppointmentNotifier appointmentNotifier;
    private final DoctorScheduleService scheduleService;

    public AppointmentResponse getAppointmentById(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        return mapper.mapToResponse(appointment);
    }

    public Page<AppointmentResponse> getAppointments(Long userId, AppointmentFilterRequest filter) {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null && filter.size() > 0 ? filter.size() : 20;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("dateTime").descending());

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
    public AppointmentResponse createAppointment(Long userId, AppointmentCreationRequest request) {
        log.info("createAppointment START: initiator={}, targetId={}, dateTime={}, consultationType={}",
                userId, request.targetId(), request.dateTime(), request.consultationType());

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-инициатор не найден"));

        User target = userRepository.findById(request.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь-цель не найден"));

        Appointment appointment = appointmentStrategyFactory.getStrategy(initiator).create(initiator, target, request);

        log.info("createAppointment: entity built with consultationType={}", appointment.getConsultationType());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        if (initiator instanceof Patient patient && target instanceof Doctor doctor) {
            assignmentService.assignIfAbsent(patient, doctor);
        }

        appointmentNotifier.onCreated(savedAppointment, initiator instanceof Doctor);

        log.info("Создана новая запись ID: {} от пользователя ID: {} к пользователю ID: {}",
                savedAppointment.getId(), userId, request.targetId());

        return mapper.mapToResponse(savedAppointment);
    }

    /**
     * Обновление записи. По бизнес-правилу метод доступен только врачу
     * (контроллер пациента не вызывает его, см. {@link
     * com.bobridze5.TeleMed_backend.api.controllers.doctor.DoctorAppointmentController}).
     *
     * Делит изменения на две группы:
     *  1) «Перенос» — изменение dateTime или consultationType. Допустим только
     *     из CREATED, до подтверждения любой из сторон, и новый слот должен
     *     существовать в расписании врача и не быть занят. После переноса
     *     пациент получает уведомление.
     *  2) Прочие поля (meetingLink/meetingPhone/meetingNotes) — врач может
     *     править их в любом нетерминальном статусе.
     */
    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, Long userId, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.NO_SHOW) {
            throw new AppointmentStateException(
                    "Нельзя изменить запись в статусе " + appointment.getStatus());
        }

        // Defense-in-depth: даже если сюда дотянется не-врач (например, после
        // ребрендинга роутинга), мы не дадим ему ничего изменить. Сравнение
        // по id, а не повторный SELECT по userRepository, — экономим запрос.
        if (!appointment.getDoctor().getId().equals(userId)) {
            throw new AccessForbiddenException("Изменять запись может только врач");
        }

        boolean reschedulingAttempt =
                (request.dateTime() != null
                        && !request.dateTime().equals(appointment.getDateTime()))
                || (request.consultationType() != null
                        && request.consultationType() != appointment.getConsultationType());

        LocalDateTime previousDateTime = appointment.getDateTime();
        ConsultationType previousType = appointment.getConsultationType();

        if (reschedulingAttempt) {
            if (appointment.getStatus() != AppointmentStatus.CREATED) {
                throw new AppointmentStateException(
                        "Изменить время можно только у неподтверждённой записи "
                                + "(текущий статус: " + appointment.getStatus() + ")");
            }
            if (Boolean.TRUE.equals(appointment.getConfirmedByPatient())
                    || Boolean.TRUE.equals(appointment.getConfirmedByDoctor())) {
                throw new AppointmentStateException(
                        "Нельзя изменить время — запись уже подтверждена одной из сторон");
            }

            // Новый слот должен существовать у врача (его расписание — источник
            // истины) и не быть занят другой активной записью. Проверяем
            // итоговую комбинацию dateTime+consultationType, даже если
            // изменилось только одно из двух полей.
            LocalDateTime newDateTime = request.dateTime() != null
                    ? request.dateTime() : previousDateTime;
            ConsultationType newType = request.consultationType() != null
                    ? request.consultationType() : previousType;

            scheduleService.validateSlotBelongsToSchedule(
                    appointment.getDoctor().getId(), newDateTime, newType);
            // Если время не меняется (поменялся только тип), то «слот занят»
            // даст ложноположительный результат на саму эту запись —
            // проверяем not-taken только при реальной смене времени.
            if (request.dateTime() != null && !request.dateTime().equals(previousDateTime)) {
                scheduleService.validateSlotNotTaken(
                        appointment.getDoctor().getId(), newDateTime);
            }
        }

        mapper.updateEntity(request, appointment);
        Appointment updated = appointmentRepository.save(appointment);

        if (reschedulingAttempt) {
            appointmentNotifier.onRescheduled(updated, previousDateTime, previousType);
        }

        log.info("Обновлена запись ID: {} пользователем ID: {} (rescheduled={})",
                appointmentId, userId, reschedulingAttempt);

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

    /**
     * Жёсткое удаление записи. Используется только для случая «врач создал
     * запись по ошибке, никто ещё не успел отреагировать». Любой другой
     * сценарий должен идти через {@link #cancelAppointment} — там сохраняется
     * история и пациент получает уведомление.
     *
     * Гейты:
     *  • удалять может только врач этой записи (defense-in-depth — контроллер
     *    уже ограничен @CurrentDoctor);
     *  • статус строго CREATED;
     *  • ни одна сторона ещё не подтвердила.
     *
     * Дополнительно: запретив удаление вне CREATED, мы автоматически
     * исключаем FK-конфликты с Review (Review.appointment NOT NULL,
     * существует только у COMPLETED).
     */
    @Transactional
    public void deleteAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (!appointment.getDoctor().getId().equals(userId)) {
            throw new AccessForbiddenException("Удалить запись может только врач");
        }

        if (appointment.getStatus() != AppointmentStatus.CREATED) {
            throw new AppointmentStateException(
                    "Удалить можно только новую неподтверждённую запись " +
                            "(текущий статус: " + appointment.getStatus() +
                            "). Используйте отмену.");
        }
        if (Boolean.TRUE.equals(appointment.getConfirmedByPatient())
                || Boolean.TRUE.equals(appointment.getConfirmedByDoctor())) {
            throw new AppointmentStateException(
                    "Нельзя удалить запись — её уже подтвердила одна из сторон. " +
                            "Используйте отмену.");
        }

        appointmentRepository.delete(appointment);

        log.info("Запись ID: {} удалена врачом ID: {}", appointmentId, userId);
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
