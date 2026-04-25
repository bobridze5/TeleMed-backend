package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.api.dto.schedule.ScheduleSlotDto;
import com.bobridze5.TeleMed_backend.api.dto.schedule.SlotResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorSchedule;
import com.bobridze5.TeleMed_backend.core.entity.medical.ScheduleSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DoctorScheduleMapper {

    public DoctorScheduleResponse mapToResponse(DoctorSchedule schedule) {
        List<SlotResponse> sortedSlots = schedule.getCustomSlots() == null
                ? List.of()
                : schedule.getCustomSlots().stream()
                .sorted(Comparator.comparing(ScheduleSlot::getStartTime)
                        .thenComparing(ScheduleSlot::getConsultationType))
                .map(this::toSlotResponse)
                .toList();
        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDayOfWeek(),
                sortedSlots
        );
    }

    public DoctorSchedule mapToEntity(DoctorScheduleRequest request) {
        List<ScheduleSlot> slots = request.customSlots() == null
                ? new ArrayList<>()
                : request.customSlots().stream()
                .map(this::toSlotEntity)
                .collect(Collectors.toCollection(ArrayList::new));
        return DoctorSchedule.builder()
                .dayOfWeek(request.dayOfWeek())
                .customSlots(slots)
                .build();
    }

    public ScheduleSlot toSlotEntity(ScheduleSlotDto dto) {
        return ScheduleSlot.builder()
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .consultationType(dto.consultationType())
                .build();
    }

    public SlotResponse toSlotResponse(ScheduleSlot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getConsultationType()
        );
    }
}
