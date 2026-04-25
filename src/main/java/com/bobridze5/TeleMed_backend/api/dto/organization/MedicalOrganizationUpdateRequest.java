package com.bobridze5.TeleMed_backend.api.dto.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record MedicalOrganizationUpdateRequest(
        @Size(max = 120)
        String name,

        @Size(max = 120)
        String address,

        @Email(message = "Неверный формат email")
        @Size(max = 60)
        String email,

        @Size(max = 60)
        String phone,

        Long cityId
) {
}
