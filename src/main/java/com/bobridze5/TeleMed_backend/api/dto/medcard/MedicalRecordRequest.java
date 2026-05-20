package com.bobridze5.TeleMed_backend.api.dto.medcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Запрос на создание записи о приёме (шаблон протокола амбулаторного приёма
 * по приказу Минздрава России №834н). Все поля кроме {@code title} опциональны —
 * врач заполняет только релевантные секции конкретного приёма.
 *
 * {@code icd10Codes} — список кодов МКБ-10, обычно выбранных из справочника
 * (autocomplete), но допустимы и кастомные строки. Хранятся в join-таблице.
 */
public record MedicalRecordRequest(
        @NotBlank(message = "Заголовок записи обязателен")
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

        /**
         * Дата следующего визита в формате ISO: YYYY-MM-DD.
         * Бэкенд принимает LocalDate (без времени и TZ); фронт должен
         * отправлять именно YYYY-MM-DD, иначе Jackson не распарсит.
         */
        LocalDate nextVisitDate,

        Long appointmentId
) {
}
