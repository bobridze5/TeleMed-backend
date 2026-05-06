package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentCreationRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.DoctorAppointmentRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.schedule.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorAppointmentStrategy implements AppointmentCreationStrategy {

    private final DoctorScheduleService scheduleService;

    @Override
    public boolean supports(User initiator) {
        return initiator instanceof Doctor;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Appointment create(User initiator, User target, AppointmentCreationRequest request) {
        if (!(request instanceof DoctorAppointmentRequest req)) {
            throw new IllegalArgumentException(
                    "DoctorAppointmentStrategy ожидает DoctorAppointmentRequest");
        }
        if (!(target instanceof Patient patient)) {
            throw new IllegalArgumentException("Доктор может записать только пациента");
        }
        Doctor doctor = (Doctor) initiator;

        scheduleService.ensureSlotExists(
                doctor.getId(), req.dateTime(),
                req.slotDurationMinutes(), req.consultationType()
        );
        scheduleService.validateSlotNotTaken(doctor.getId(), req.dateTime());

        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .dateTime(req.dateTime())
                .consultationType(req.consultationType())
                .status(AppointmentStatus.CREATED)
                .build();
    }
}
