package com.bobridze5.TeleMed_backend.api.dto.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalOrganizationRequest(
        @NotBlank(message = "Название обязательно")
        @Size(max = 120)
        String name,

        @NotBlank(message = "Адрес обязателен")
        @Size(max = 120)
        String address,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Неверный формат email")
        @Size(max = 60)
        String email,

        @NotBlank(message = "Телефон обязателен")
        @Size(max = 60)
        String phone,

        @NotNull(message = "Город обязателен")
        Long cityId
) {
}
