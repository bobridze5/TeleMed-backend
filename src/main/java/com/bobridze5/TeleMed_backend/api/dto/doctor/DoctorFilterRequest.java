package com.bobridze5.TeleMed_backend.api.dto.doctor;

import lombok.Data;

@Data
public class DoctorFilterRequest {
    private Long specializationId;
    private Long cityId;
}
