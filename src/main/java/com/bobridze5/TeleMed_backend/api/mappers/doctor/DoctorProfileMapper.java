package com.bobridze5.TeleMed_backend.api.mappers.doctor;

import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.PatientDoctorAssignment;
import org.springframework.stereotype.Component;

@Component
public class DoctorProfileMapper {

    public DoctorResponse mapToResponse(Doctor doctor) {
        String specialization = doctor.getSpecialization() != null
                ? doctor.getSpecialization().getName() : null;
        String organization = doctor.getOrganization() != null
                ? doctor.getOrganization().getName() : null;
        String city = doctor.getOrganization() != null && doctor.getOrganization().getCity() != null
                ? doctor.getOrganization().getCity().getName() : null;

        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getMiddleName(),
                doctor.getEmail(),
                doctor.getExperience(),
                doctor.getQualification(),
                specialization,
                organization,
                city
        );
    }

    public DoctorPatientResponse mapToPatientResponse(PatientDoctorAssignment assignment) {
        var patient = assignment.getPatient();
        return new DoctorPatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getMiddleName(),
                patient.getDiabetesType(),
                patient.getDiagnosisDate(),
                assignment.getAssignedAt()
        );
    }

    public void updateDoctor(Doctor doctor, DoctorProfileUpdateRequest request) {
        if (request.getQualification() != null) doctor.setQualification(request.getQualification());
    }
}
