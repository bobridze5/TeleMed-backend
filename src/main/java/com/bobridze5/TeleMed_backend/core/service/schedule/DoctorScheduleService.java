package com.bobridze5.TeleMed_backend.core.service.schedule;

import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DoctorScheduleService {
    List<DoctorScheduleResponse> getSchedule(Long doctorId);

    List<DoctorScheduleResponse> setSchedule(Long doctorId, List<DoctorScheduleRequest> requests);

    List<LocalDateTime> getAvailableSlots(Long doctorId, LocalDate date);

    void validateSlot(Long doctorId, LocalDateTime dateTime);
}
