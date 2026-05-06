package com.bobridze5.TeleMed_backend.api.mappers.doctor;

import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.medical.PatientDoctorAssignment;
import org.springframework.stereotype.Component;

@Component
public class DoctorProfileMapper {

    public DoctorResponse mapToResponse(Doctor doctor) {
        return mapToResponse(doctor, null, null);
    }

    /**
     * Версия маппера, позволяющая пробросить агрегат рейтинга (средняя оценка +
     * количество отзывов). Если статистика не передана (null), в ответе будут
     * null-поля.
     */
    public DoctorResponse mapToResponse(Doctor doctor, Double averageRating, Long reviewsCount) {
        String specialization = doctor.getSpecialization() != null
                ? doctor.getSpecialization().getName() : null;
        String organization = doctor.getOrganization() != null
                ? doctor.getOrganization().getName() : null;
        String city = doctor.getOrganization() != null && doctor.getOrganization().getCity() != null
                ? doctor.getOrganization().getCity().getName() : null;

        // Нормализуем рейтинг: если отзывов 0 — возвращаем null, иначе округляем до 0.1.
        Double avg = null;
        Long count = 0L;
        if (reviewsCount != null && reviewsCount > 0 && averageRating != null) {
            avg = Math.round(averageRating * 10.0) / 10.0;
            count = reviewsCount;
        }

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
                city,
                doctor.getAbout(),
                avg,
                count
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

    public DoctorPatientResponse mapPatientToResponse(Patient patient) {
        return new DoctorPatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getMiddleName(),
                patient.getDiabetesType(),
                patient.getDiagnosisDate(),
                null
        );
    }

    public void updateDoctor(Doctor doctor, DoctorProfileUpdateRequest request) {
        if (request.getQualification() != null) doctor.setQualification(request.getQualification());
        if (request.getExperience() != null) doctor.setExperience(request.getExperience());
    }
}
