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
        String city,
        String about,
        // Средняя оценка по 5-балльной шкале и общее кол-во отзывов. Считается
        // на бэкенде, чтобы фронт не делал отдельный запрос за каждым врачом.
        // null если отзывов нет.
        Double averageRating,
        Long reviewsCount
) {
}
