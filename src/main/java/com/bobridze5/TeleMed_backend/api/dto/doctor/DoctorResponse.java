package com.bobridze5.TeleMed_backend.api.dto.doctor;

public record DoctorResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String email,
        Integer experience,
        String qualification,
        String specialization,
        String organization,
        String city
) {
}
