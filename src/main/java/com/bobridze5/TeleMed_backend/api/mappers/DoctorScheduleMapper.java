package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {

    public DoctorScheduleResponse mapToResponse(DoctorSchedule schedule) {
        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSlotDurationMinutes()
        );
    }

    public DoctorSchedule mapToEntity(DoctorScheduleRequest request) {
        return DoctorSchedule.builder()
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .slotDurationMinutes(request.slotDurationMinutes())
                .build();
    }
}
