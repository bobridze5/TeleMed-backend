package com.bobridze5.TeleMed_backend.core.service.doctor;

import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.AccessForbiddenException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.PatientDoctorAssignmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorPatientAccessService {
    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public Patient getPatientForDoctor(Doctor doctor, Long patientId) {
        if (!assignmentRepository.existsAssignment(doctor.getId(), patientId)) {
            throw new AccessForbiddenException("Пациент не прикреплён к данному врачу");
        }

        return userRepository.findById(patientId)
                .filter(u -> u instanceof Patient)
                .map(u -> (Patient) u)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));
    }
}
