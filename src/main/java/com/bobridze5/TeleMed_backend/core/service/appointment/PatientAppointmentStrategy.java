package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentCreationRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.PatientAppointmentRequest;
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

/**
 * Создание записи, инициированной пациентом. Слот должен уже существовать
 * в расписании врача и быть свободен — пациент не имеет права изобретать
 * новые слоты «на лету».
 */
@Service
@RequiredArgsConstructor
public class PatientAppointmentStrategy implements AppointmentCreationStrategy {

    private final DoctorScheduleService scheduleService;

    @Override
    public boolean supports(User initiator) {
        return initiator instanceof Patient;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Appointment create(User initiator, User target, AppointmentCreationRequest request) {
        if (!(request instanceof PatientAppointmentRequest req)) {
            throw new IllegalArgumentException(
                    "PatientAppointmentStrategy ожидает PatientAppointmentRequest");
        }
        if (!(target instanceof Doctor doctor)) {
            throw new IllegalArgumentException("Пациент может записаться только к доктору");
        }
        Patient patient = (Patient) initiator;

        // Пациент может занимать только заранее объявленный врачом слот
        // нужного типа консультации.
        scheduleService.validateSlot(doctor.getId(), req.dateTime(), req.consultationType());

        // Создаём запись без подтверждений: и пациент, и врач должны явно
        // нажать «Подтвердить» в своих интерфейсах. Только тогда запись
        // переходит в статус CONFIRMED. Поля confirmedByPatient/Doctor
        // инициализируются false автоматически через @Builder.Default.
        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .dateTime(req.dateTime())
                .consultationType(req.consultationType())
                .status(AppointmentStatus.CREATED)
                .build();
    }
}
