package com.bobridze5.TeleMed_backend.api.dto.medcard;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * PATCH-запрос: все поля опциональны, обновляются только non-null.
 * Чтобы очистить текстовое поле — фронт присылает пустую строку, бэк
 * сохранит её как есть; в UI отображения пустые поля просто не рендерятся.
 *
 * Для {@code icd10Codes} принят отдельный контракт: null = не менять,
 * пустой список = очистить, заполненный список = заменить целиком.
 */
public record MedicalRecordUpdateRequest(
        @Size(max = 255)
        String title,

        @Size(max = 5000)
        String complaints,

        @Size(max = 5000)
        String anamnesisMorbi,

        @Size(max = 5000)
        String anamnesisVitae,

        @Size(max = 5000)
        String objectiveStatus,

        @Size(max = 5000)
        String localStatus,

        @Size(max = 5000)
        String diagnosis,

        List<@Size(max = 20) String> icd10Codes,

        @Size(max = 5000)
        String examinationPlan,

        @Size(max = 5000)
        String recommendations,

        @Size(max = 5000)
        String prescriptions,

        LocalDate nextVisitDate
) {
}
