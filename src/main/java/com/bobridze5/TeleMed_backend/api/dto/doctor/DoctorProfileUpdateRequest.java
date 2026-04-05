package com.bobridze5.TeleMed_backend.api.dto.doctor;

import com.bobridze5.TeleMed_backend.api.dto.profile.UserProfileUpdateRequest;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DoctorProfileUpdateRequest extends UserProfileUpdateRequest {

    @Size(min = 1, max = 500, message = "Длина квалификации должна быть от 1 до 500 символов")
    private String qualification;
}
