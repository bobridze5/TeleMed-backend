package com.bobridze5.TeleMed_backend.core.service.doctor;

import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.medical.PatientDoctorAssignment;
import com.bobridze5.TeleMed_backend.core.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Управление привязками пациент - врач.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    /**
     * Идемпотентно создаёт привязку пациента к врачу, если её ещё нет.
     * Если запись уже существует — ничего не делает.
     *
     * @return true, если привязка была создана; false, если уже существовала
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean assignIfAbsent(Patient patient, Doctor doctor) {
        if (assignmentRepository.existsAssignment(doctor.getId(), patient.getId())) {
            return false;
        }

        assignmentRepository.save(PatientDoctorAssignment.builder()
                .patient(patient)
                .doctor(doctor)
                .build());
        log.info("Создана привязка пациент ID:{} к врачу ID:{}", patient.getId(), doctor.getId());
        return true;
    }
}
