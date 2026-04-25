package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalCardResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.doctor.DoctorPatientAccessService;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicalCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API.PATIENT_CARD)
@RequiredArgsConstructor
@Tag(name = "Медкарта пациента (врач)", description = "Просмотр медкарты прикреплённого пациента")
public class DoctorMedicalCardController {
    private final MedicalCardService cardService;
    private final DoctorPatientAccessService accessService;

    @GetMapping
    @Operation(summary = "Медкарта пациента")
    public MedicalCardResponse getPatientCard(
            @CurrentDoctor Doctor doctor,
            @PathVariable Long patientId
    ) {
        Patient patient = accessService.getPatientForDoctor(doctor, patientId);
        return cardService.getCard(patient);
    }
}
