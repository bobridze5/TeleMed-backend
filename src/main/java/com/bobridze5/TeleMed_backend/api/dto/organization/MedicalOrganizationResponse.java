package com.bobridze5.TeleMed_backend.api.dto.organization;

public record MedicalOrganizationResponse(
        Long id,
        String name,
        String address,
        String email,
        String phone,
        Long cityId,
        String cityName
) {
}
