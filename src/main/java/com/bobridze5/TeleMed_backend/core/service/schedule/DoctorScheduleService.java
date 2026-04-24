package com.bobridze5.TeleMed_backend.core.service.schedule;

import com.bobridze5.TeleMed_backend.api.dto.schedule.AvailableSlotDto;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleRequest;
import com.bobridze5.TeleMed_backend.api.dto.schedule.DoctorScheduleResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DoctorScheduleService {
    List<DoctorScheduleResponse> getSchedule(Long doctorId);

    List<DoctorScheduleResponse> setSchedule(Long doctorId, List<DoctorScheduleRequest> requests);

    List<AvailableSlotDto> getAvailableSlots(Long doctorId, LocalDate date);

    void validateSlot(Long doctorId, LocalDateTime dateTime, ConsultationType consultationType);
}
