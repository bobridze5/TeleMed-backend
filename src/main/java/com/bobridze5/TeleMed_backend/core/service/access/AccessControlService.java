package com.bobridze5.TeleMed_backend.core.service.access;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.repository.PatientDoctorAssignmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {
    private final PatientDoctorAssignmentRepository patientDoctorAssignmentRepository;

    public void checkAccess(User user, Long patientId) {

        if (user.hasPatientProfile() &&
                user.getPatient().isIdentifiedBy(patientId)) {
            return;
        }

        if (user.hasDoctorProfile() &&
                patientDoctorAssignmentRepository.existsAssignment(
                        user.getDoctor().getDoctorId(),
                        patientId
                )) {
            return;
        }

        throw new AccessDeniedException("Access denied");
    }

}
