package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.*;

public record RegisterDoctorRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 64, message = "Пароль должен быть от 8 до 64 символов")
        String password1,

        @NotBlank(message = "Пароль не может быть пустым")
        String password2,

        @Size(min = 2, max = 120, message = "Длина должна быть от 2 до 120 символов")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(min = 2, max = 120, message = "Длина должна быть от 2 до 120 символов")
        String lastName,

        @Size(min = 2, max = 120, message = "Длина должна быть от 2 до 120 символов")
        String middleName,

        @NotNull(message = "Специализация обязательна")
        Long specializationId,

        Long organizationId,

        @NotNull(message = "Стаж обязателен")
        @Min(value = 0, message = "Стаж не может быть отрицательным")
        @Max(value = 60, message = "Стаж не может превышать 60 лет")
        Integer experience,

        @NotBlank(message = "Квалификация обязательна")
        @Size(max = 500, message = "Квалификация не может превышать 500 символов")
        String qualification
) {
    public RegisterDoctorRequest {
        if (!password1.equals(password2)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
    }
}
