package com.bobridze5.TeleMed_backend.core.service.schedule;

import com.bobridze5.TeleMed_backend.api.dto.schedule.AvailableSlotDto;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.api.mappers.DoctorScheduleMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorSchedule;
import com.bobridze5.TeleMed_backend.core.entity.medical.ScheduleSlot;
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
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorScheduleMapper mapper;

    public List<DoctorScheduleResponse> getSchedule(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(mapper::mapToResponse)
                .toList();
    }

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

    public List<AvailableSlotDto> getAvailableSlots(Long doctorId, LocalDate date) {
        return scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .map(schedule -> filterAvailableSlots(doctorId, date, schedule))
                .orElse(List.of());
    }

    public void validateSlot(Long doctorId, LocalDateTime dateTime, ConsultationType consultationType) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dateTime.getDayOfWeek())
                .orElseThrow(() -> new InvalidSlotException("Врач не ведёт приём в этот день недели"));

        boolean isValidSlot = generateSlots(dateTime.toLocalDate(), schedule).stream()
                .anyMatch(slot -> slot.startDateTime().equals(dateTime)
                        && slot.consultationType() == consultationType);
        if (!isValidSlot) {
            throw new InvalidSlotException("Выбранное время не соответствует доступным слотам врача");
        }

        if (appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctorId, dateTime, AppointmentStatus.CANCELED)) {
            throw new InvalidSlotException("Выбранный слот уже занят");
        }
    }

    private List<AvailableSlotDto> filterAvailableSlots(Long doctorId, LocalDate date, DoctorSchedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        return generateSlots(date, schedule).stream()
                .filter(slot -> slot.startDateTime().isAfter(now))
                .filter(slot -> !appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                        doctorId, slot.startDateTime(), AppointmentStatus.CANCELED))
                .toList();
    }

    private List<AvailableSlotDto> generateSlots(LocalDate date, DoctorSchedule schedule) {
        if (schedule.getCustomSlots() != null && !schedule.getCustomSlots().isEmpty()) {
            return schedule.getCustomSlots().stream()
                    .sorted(Comparator.comparing(ScheduleSlot::getStartTime)
                            .thenComparing(ScheduleSlot::getConsultationType))
                    .map(s -> new AvailableSlotDto(
                            LocalDateTime.of(date, s.getStartTime()),
                            LocalDateTime.of(date, s.getEndTime()),
                            s.getConsultationType()
                    ))
                    .toList();
        }

        List<AvailableSlotDto> slots = new ArrayList<>();
        LocalTime current = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        int duration = schedule.getSlotDurationMinutes();

        while (!current.plusMinutes(duration).isAfter(end)) {
            LocalTime slotEnd = current.plusMinutes(duration);
            slots.add(new AvailableSlotDto(
                    LocalDateTime.of(date, current),
                    LocalDateTime.of(date, slotEnd),
                    ConsultationType.VIDEO
            ));
            current = slotEnd;
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
