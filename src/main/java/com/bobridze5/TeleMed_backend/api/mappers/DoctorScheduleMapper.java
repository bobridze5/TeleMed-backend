package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.api.dto.schedule.ScheduleSlotDto;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorSchedule;
import com.bobridze5.TeleMed_backend.core.entity.medical.ScheduleSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DoctorScheduleMapper {

    public DoctorScheduleResponse mapToResponse(DoctorSchedule schedule) {
        List<ScheduleSlotDto> sortedSlots = schedule.getCustomSlots() == null
                ? List.of()
                : schedule.getCustomSlots().stream()
                        .sorted(Comparator.comparing(ScheduleSlot::getStartTime)
                                .thenComparing(ScheduleSlot::getConsultationType))
                        .map(s -> new ScheduleSlotDto(s.getStartTime(), s.getEndTime(), s.getConsultationType()))
                        .toList();
        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSlotDurationMinutes(),
                sortedSlots
        );
    }

    public DoctorSchedule mapToEntity(DoctorScheduleRequest request) {
        List<ScheduleSlot> slots = request.customSlots() == null ? new ArrayList<>()
                : request.customSlots().stream()
                        .map(dto -> ScheduleSlot.builder()
                                .startTime(dto.startTime())
                                .endTime(dto.endTime())
                                .consultationType(dto.consultationType())
                                .build())
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return DoctorSchedule.builder()
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .slotDurationMinutes(request.slotDurationMinutes())
                .customSlots(slots)
                .build();
    }
}
