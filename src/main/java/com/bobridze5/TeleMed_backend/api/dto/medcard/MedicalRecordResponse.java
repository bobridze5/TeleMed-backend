package com.bobridze5.TeleMed_backend.api.dto.medcard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Полная запись о приёме (шаблон протокола 834н).
 *
 * {@code icd10Codes} — выбранные врачом коды (как они хранятся);
 * {@code icd10CodeDetails} — расширенная инфа из справочника
 * (название и т. п.) для тех кодов, которые удалось найти. Это
 * избавляет фронт от лишнего запроса к справочнику для рендера badge.
 * Кастомный код, не лежащий в справочнике, попадёт только в icd10Codes.
 */
public record MedicalRecordResponse(
        Long id,
        Long patientId,
        Long doctorId,
        String doctorName,
        Long appointmentId,
        String title,
        String complaints,
        String anamnesisMorbi,
        String anamnesisVitae,
        String objectiveStatus,
        String localStatus,
        String diagnosis,
        List<String> icd10Codes,
        List<Icd10CodeResponse> icd10CodeDetails,
        String examinationPlan,
        String recommendations,
        String prescriptions,
        LocalDate nextVisitDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
