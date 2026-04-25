package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.medcard.MedicalCardResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.medcard.MedicalCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API.PATIENT_ME_MEDICAL_CARD)
@RequiredArgsConstructor
@Tag(name = "Медкарта пациента", description = "Агрегированный просмотр своей медкарты")
public class MedicalController {
    private final MedicalCardService cardService;

    @GetMapping
    @Operation(summary = "Моя медкарта (агрегат)")
    public MedicalCardResponse getMyCard(@CurrentPatient Patient patient) {
        return cardService.getCard(patient);
    }
}
