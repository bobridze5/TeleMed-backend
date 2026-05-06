package com.bobridze5.TeleMed_backend.core.service.schedule;

import com.bobridze5.TeleMed_backend.api.dto.schedule.AutoGenerateRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.AvailableSlotDto;
import com.bobridze5.TeleMed_backend.api.dto.schedule.CreateOrUpdateSlotRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.api.dto.schedule.SlotResponse;
import com.bobridze5.TeleMed_backend.api.mappers.DoctorScheduleMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorSchedule;
import com.bobridze5.TeleMed_backend.core.entity.medical.ScheduleSlot;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.exceptions.InvalidSlotException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
import com.bobridze5.TeleMed_backend.core.repository.DoctorScheduleRepository;
import com.bobridze5.TeleMed_backend.core.repository.ScheduleSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final ScheduleSlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleMapper mapper;

    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getSchedule(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoctorScheduleResponse getScheduleByDayOfWeek(Long doctorId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .map(mapper::mapToResponse)
                .orElseGet(() -> new DoctorScheduleResponse(null, dayOfWeek, List.of()));
    }

    @Transactional
    public List<DoctorScheduleResponse> setSchedule(Long doctorId, List<DoctorScheduleRequest> requests) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Врач не найден"));

        for (DoctorScheduleRequest req : requests) {
            List<ScheduleSlot> slots = req.customSlots() == null ? List.of()
                    : req.customSlots().stream().map(mapper::toSlotEntity).toList();
            validateNoOverlapsWithin(slots);
        }

        scheduleRepository.deleteByDoctorId(doctorId);

        List<DoctorSchedule> schedules = requests.stream()
                .map(req -> {
                    DoctorSchedule schedule = mapper.mapToEntity(req);
                    schedule.setDoctor(doctor);
                    schedule.getCustomSlots().forEach(slot -> slot.setSchedule(schedule));
                    return schedule;
                })
                .toList();

        List<DoctorSchedule> saved = scheduleRepository.saveAll(schedules);
        log.info("Расписание врача ID={} обновлено: {} дней", doctorId, saved.size());

        return saved.stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    @Transactional
    public SlotResponse addSlot(Long doctorId, DayOfWeek dayOfWeek, CreateOrUpdateSlotRequest request) {
        validateSlotBoundaries(request.startTime(), request.endTime());

        DoctorSchedule schedule = getOrCreateSchedule(doctorId, dayOfWeek);

        for (ScheduleSlot existing : schedule.getCustomSlots()) {
            if (slotsOverlap(request.startTime(), request.endTime(),
                    existing.getStartTime(), existing.getEndTime())) {
                throw new InvalidSlotException("Слот пересекается с существующим: "
                        + existing.getStartTime() + "–" + existing.getEndTime());
            }
        }

        ScheduleSlot slot = ScheduleSlot.builder()
                .schedule(schedule)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .consultationType(request.consultationType())
                .build();

        ScheduleSlot saved = slotRepository.save(slot);
        log.info("Слот добавлен: doctorId={}, dayOfWeek={}, {}–{}", doctorId, dayOfWeek,
                saved.getStartTime(), saved.getEndTime());
        return mapper.toSlotResponse(saved);
    }

    @Transactional
    public SlotResponse updateSlot(Long doctorId, Long slotId, CreateOrUpdateSlotRequest request) {
        validateSlotBoundaries(request.startTime(), request.endTime());

        ScheduleSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Слот не найден"));

        DoctorSchedule schedule = slot.getSchedule();
        if (!schedule.getDoctor().getId().equals(doctorId)) {
            throw new EntityNotFoundException("Слот не найден");
        }

        for (ScheduleSlot existing : schedule.getCustomSlots()) {
            if (existing.getId().equals(slotId)) continue;
            if (slotsOverlap(request.startTime(), request.endTime(),
                    existing.getStartTime(), existing.getEndTime())) {
                throw new InvalidSlotException("Слот пересекается с существующим: "
                        + existing.getStartTime() + "–" + existing.getEndTime());
            }
        }

        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());
        slot.setConsultationType(request.consultationType());

        ScheduleSlot saved = slotRepository.save(slot);
        log.info("Слот обновлён: doctorId={}, slotId={}", doctorId, slotId);
        return mapper.toSlotResponse(saved);
    }

    @Transactional
    public void deleteSlot(Long doctorId, Long slotId) {
        ScheduleSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Слот не найден"));

        Long scheduleDoctorId = slot.getSchedule().getDoctor().getId();
        if (!scheduleDoctorId.equals(doctorId)) {
            throw new EntityNotFoundException("Слот не найден");
        }

        slotRepository.delete(slot);
        log.info("Слот удалён: doctorId={}, slotId={}", doctorId, slotId);
    }

    /**
     * Генерирует слоты от fromTime до toTime с шагом slotMinutes.
     * Кандидаты, пересекающиеся с уже существующими customSlots дня, пропускаются.
     * Существующие слоты сохраняются без изменений.
     */
    @Transactional
    public DoctorScheduleResponse autoGenerate(Long doctorId, DayOfWeek dayOfWeek, AutoGenerateRequest request) {
        if (!request.fromTime().isBefore(request.toTime())) {
            throw new InvalidSlotException("fromTime должно быть раньше toTime");
        }

        DoctorSchedule schedule = getOrCreateSchedule(doctorId, dayOfWeek);
        List<ScheduleSlot> existing = new ArrayList<>(schedule.getCustomSlots());

        int step = request.slotMinutes();
        LocalTime cursor = request.fromTime();
        int generated = 0, skipped = 0;

        while (!cursor.plusMinutes(step).isAfter(request.toTime())) {
            LocalTime slotStart = cursor;
            LocalTime slotEnd = cursor.plusMinutes(step);

            boolean overlapsExisting = existing.stream()
                    .anyMatch(s -> slotsOverlap(slotStart, slotEnd, s.getStartTime(), s.getEndTime()));

            if (!overlapsExisting) {
                ScheduleSlot slot = ScheduleSlot.builder()
                        .schedule(schedule)
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .consultationType(request.consultationType())
                        .build();
                ScheduleSlot saved = slotRepository.save(slot);
                existing.add(saved);
                generated++;
            } else {
                skipped++;
            }
            cursor = slotEnd;
        }

        log.info("Авто-генерация: doctorId={}, dayOfWeek={}, добавлено={}, пропущено={}",
                doctorId, dayOfWeek, generated, skipped);

        DoctorSchedule refreshed = scheduleRepository.findById(schedule.getId())
                .orElseThrow(() -> new EntityNotFoundException("Расписание не найдено"));
        return mapper.mapToResponse(refreshed);
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotDto> getAvailableSlots(Long doctorId, LocalDate date) {
        return scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .map(schedule -> filterAvailableSlots(doctorId, date, schedule))
                .orElse(List.of());
    }

    /**
     * Полная валидация слота для пациент-инициированной записи: и «слот есть
     * в расписании», и «на него ещё никто не записан».
     */
    public void validateSlot(Long doctorId, LocalDateTime dateTime, ConsultationType consultationType) {
        validateSlotBelongsToSchedule(doctorId, dateTime, consultationType);
        validateSlotNotTaken(doctorId, dateTime);
    }

    /**
     * Проверяет, что слот (startTime + consultationType) действительно есть в
     * расписании врача на этот день недели. Для пациент-flow обязателен:
     * пациент должен выбирать только из слотов, которые врач объявил.
     */
    public void validateSlotBelongsToSchedule(Long doctorId, LocalDateTime dateTime, ConsultationType consultationType) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dateTime.getDayOfWeek())
                .orElseThrow(() -> new InvalidSlotException("Врач не ведёт приём в этот день недели"));

        boolean isValidSlot = schedule.getCustomSlots().stream()
                .anyMatch(slot -> slot.getStartTime().equals(dateTime.toLocalTime())
                        && slot.getConsultationType() == consultationType);
        if (!isValidSlot) {
            throw new InvalidSlotException("Выбранное время не соответствует доступным слотам врача");
        }
    }

    /**
     * Проверяет, что на указанное время ещё нет активной (не отменённой) записи
     * у этого врача. Используется и в пациент-flow, и во врач-flow (чтобы
     * врач сам себя не забукировал дважды).
     */
    public void validateSlotNotTaken(Long doctorId, LocalDateTime dateTime) {
        if (appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId, dateTime, AppointmentStatus.CANCELED)) {
            throw new InvalidSlotException("Выбранный слот уже занят");
        }
    }

    /**
     * Гарантирует, что в расписании врача есть слот на указанное время и тип
     * консультации. Используется, когда врач самостоятельно записывает
     * пациента на приём — тогда запись создаёт слот в расписании «на лету»,
     * и этот слот становится частью обычного расписания (виден в разделе
     * «Расписание», доступен другим пациентам для просмотра).
     *
     * Поведение:
     *  • точное совпадение (start/end/type) — no-op;
     *  • пересечение с любым другим слотом — {@link InvalidSlotException}
     *    («никаких пересечений в расписании не должно быть»);
     *  • иначе — создаёт новый слот и кладёт его в in-memory коллекцию
     *    расписания, чтобы последующий validateSlot увидел его в той же
     *    транзакции.
     */
    @Transactional
    public void ensureSlotExists(Long doctorId, LocalDateTime dateTime,
                                 int durationMinutes, ConsultationType consultationType) {
        if (durationMinutes <= 0) durationMinutes = 30;
        LocalTime startTime = dateTime.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        DoctorSchedule schedule = getOrCreateSchedule(doctorId, dateTime.getDayOfWeek());

        // Точное совпадение (start/end/type) — слот уже есть, нет смысла
        // создавать дубликат.
        boolean exactMatch = schedule.getCustomSlots().stream()
                .anyMatch(s -> s.getStartTime().equals(startTime)
                        && s.getEndTime().equals(endTime)
                        && s.getConsultationType() == consultationType);
        if (exactMatch) return;

        // Пересечение с любым существующим слотом (того же или другого типа).
        // Принцип: в расписании не должно быть пересекающихся слотов — иначе
        // врач не сможет понять, что у него реально свободно.
        for (ScheduleSlot existing : schedule.getCustomSlots()) {
            if (slotsOverlap(startTime, endTime, existing.getStartTime(), existing.getEndTime())) {
                throw new InvalidSlotException(
                        "Время " + startTime + "–" + endTime
                                + " пересекается со слотом " + existing.getStartTime()
                                + "–" + existing.getEndTime() + " в вашем расписании");
            }
        }

        ScheduleSlot slot = ScheduleSlot.builder()
                .schedule(schedule)
                .startTime(startTime)
                .endTime(endTime)
                .consultationType(consultationType)
                .build();
        ScheduleSlot saved = slotRepository.save(slot);
        // Важно: добавляем слот в уже загруженную коллекцию schedule, иначе
        // следующий вызов в той же транзакции (напр. validateSlot) его не
        // увидит из first-level кэша Hibernate и упадёт с «Выбранное время не
        // соответствует доступным слотам врача».
        schedule.getCustomSlots().add(saved);
        log.info("Auto-slot: создан слот на {} у врача ID={} (type={}, {}-{})",
                dateTime.getDayOfWeek(), doctorId, consultationType, startTime, endTime);
    }

    private DoctorSchedule getOrCreateSchedule(Long doctorId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .orElseGet(() -> {
                    Doctor doctor = doctorRepository.findById(doctorId)
                            .orElseThrow(() -> new EntityNotFoundException("Врач не найден"));
                    DoctorSchedule s = DoctorSchedule.builder()
                            .doctor(doctor)
                            .dayOfWeek(dayOfWeek)
                            .build();
                    return scheduleRepository.save(s);
                });
    }

    private void validateSlotBoundaries(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new InvalidSlotException("Время начала и окончания обязательны");
        }
        if (!start.isBefore(end)) {
            throw new InvalidSlotException("Время начала должно быть раньше времени окончания");
        }
    }

    private void validateNoOverlapsWithin(List<ScheduleSlot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ScheduleSlot a = slots.get(i);
            validateSlotBoundaries(a.getStartTime(), a.getEndTime());
            for (int j = i + 1; j < slots.size(); j++) {
                ScheduleSlot b = slots.get(j);
                if (slotsOverlap(a.getStartTime(), a.getEndTime(), b.getStartTime(), b.getEndTime())) {
                    throw new InvalidSlotException("Слоты пересекаются: "
                            + a.getStartTime() + "–" + a.getEndTime() + " и "
                            + b.getStartTime() + "–" + b.getEndTime());
                }
            }
        }
    }

    /**
     * Слоты пересекаются, если интервалы [a, b) накладываются (касание границы — НЕ пересечение).
     */
    private boolean slotsOverlap(LocalTime a1, LocalTime a2, LocalTime b1, LocalTime b2) {
        return a1.isBefore(b2) && b1.isBefore(a2);
    }

    private List<AvailableSlotDto> filterAvailableSlots(Long doctorId, LocalDate date, DoctorSchedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        return schedule.getCustomSlots().stream()
                .sorted(Comparator.comparing(ScheduleSlot::getStartTime)
                        .thenComparing(ScheduleSlot::getConsultationType))
                .map(s -> new AvailableSlotDto(
                        LocalDateTime.of(date, s.getStartTime()),
                        LocalDateTime.of(date, s.getEndTime()),
                        s.getConsultationType()
                ))
                .filter(slot -> slot.startDateTime().isAfter(now))
                .filter(slot -> !appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                        doctorId, slot.startDateTime(), AppointmentStatus.CANCELED))
                .toList();
    }
}
