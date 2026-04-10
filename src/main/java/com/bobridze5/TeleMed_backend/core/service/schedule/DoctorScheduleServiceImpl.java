package com.bobridze5.TeleMed_backend.core.service.schedule;

import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.api.mappers.DoctorScheduleMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorSchedule;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.exceptions.InvalidSlotException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.DoctorScheduleRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorScheduleMapper mapper;

    @Override
    public List<DoctorScheduleResponse> getSchedule(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<DoctorScheduleResponse> setSchedule(Long doctorId, List<DoctorScheduleRequest> requests) {
        Doctor doctor = findDoctor(doctorId);

        scheduleRepository.deleteByDoctorId(doctorId);

        List<DoctorSchedule> schedules = requests.stream()
                .map(req -> {
                    DoctorSchedule schedule = mapper.mapToEntity(req);
                    schedule.setDoctor(doctor);
                    return schedule;
                })
                .toList();

        List<DoctorSchedule> saved = scheduleRepository.saveAll(schedules);
        log.info("Расписание врача ID={} обновлено: {} дней", doctorId, saved.size());

        return saved.stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    @Override
    public List<LocalDateTime> getAvailableSlots(Long doctorId, LocalDate date) {
        return scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .map(schedule -> filterAvailableSlots(doctorId, date, schedule))
                .orElse(List.of());
    }

    @Override
    public void validateSlot(Long doctorId, LocalDateTime dateTime) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dateTime.getDayOfWeek())
                .orElseThrow(() -> new InvalidSlotException("Врач не ведёт приём в этот день недели"));

        boolean isValidSlot = generateSlots(dateTime.toLocalDate(), schedule).contains(dateTime);
        if (!isValidSlot) {
            throw new InvalidSlotException("Выбранное время не соответствует доступным слотам врача");
        }

        if (appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId, dateTime, AppointmentStatus.CANCELED)) {
            throw new InvalidSlotException("Выбранный слот уже занят");
        }
    }

    private List<LocalDateTime> filterAvailableSlots(Long doctorId, LocalDate date, DoctorSchedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        return generateSlots(date, schedule).stream()
                .filter(slot -> slot.isAfter(now))
                .filter(slot -> !appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                        doctorId, slot, AppointmentStatus.CANCELED))
                .toList();
    }

    // Генерирует все слоты для дня по расписанию.
    // Слот добавляется, если слот + его длительность не выходит за конец рабочего дня.
    // Пример: 09:00–12:00, 30 мин => [09:00, 09:30, 10:00, 10:30, 11:00, 11:30]
    private List<LocalDateTime> generateSlots(LocalDate date, DoctorSchedule schedule) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalTime current = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        int duration = schedule.getSlotDurationMinutes();

        while (!current.plusMinutes(duration).isAfter(end)) {
            slots.add(LocalDateTime.of(date, current));
            current = current.plusMinutes(duration);
        }

        return slots;
    }

    private Doctor findDoctor(Long doctorId) {
        return userRepository.findById(doctorId)
                .filter(u -> u instanceof Doctor)
                .map(u -> (Doctor) u)
                .orElseThrow(() -> new EntityNotFoundException("Врач не найден"));
    }
}
