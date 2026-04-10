package com.bobridze5.TeleMed_backend.api.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public abstract class UserProfileUpdateRequest {
    @Size(min = 2, max = 120, message = "Длина должна быть от 2 до 120 символов")
    private String firstName;

    @Size(min = 2, max = 120, message = "Длина должна быть от 2 до 120 символов")
    private String lastName;

    @Size(min = 2, max = 120, message = "Длина должна быть от 2 до 120 символов")
    private String middleName;

    @Past(message = "Дата должна быть в прошлом")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateBirth;

    @Pattern(regexp = "[MF]", message = "Допустимые значения: M, F")
    private String gender;

    @Email(message = "Невалидный email")
    private String email;

    @Size(min = 8, max = 64, message = "Длина пароля должна быть в пределах от 8 до 64 символов")
    private String password;
}