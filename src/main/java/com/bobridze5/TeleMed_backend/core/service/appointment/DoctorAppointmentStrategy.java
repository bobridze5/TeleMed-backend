package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorAppointmentStrategy implements AppointmentCreationStrategy {
    @Override
    public boolean supports(User initiator) {
        return initiator instanceof Doctor;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Appointment create(User initiator, User target, AppointmentRequest request) {
        if (!(target instanceof Patient patient)) {
            throw new IllegalArgumentException("Доктор может записать только пациента");
        }

        Doctor doctor = (Doctor) initiator;

        // Создаём запись без подтверждений: и пациент, и врач должны явно
        // нажать «Подтвердить» в своих интерфейсах. Только тогда запись
        // переходит в статус CONFIRMED.
        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .dateTime(request.dateTime())
                .consultationType(request.consultationType())
                .status(AppointmentStatus.CREATED)
                .confirmedByPatient(false)
                .confirmedByDoctor(false)
                .build();
    }
}
