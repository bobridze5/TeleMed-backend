package com.bobridze5.TeleMed_backend.api.dto.medcard;

/**
 * DTO одного кода МКБ-10 для autocomplete на фронте.
 * {@code parent} нужен, чтобы UI мог показать подсказку «E11.9 относится к E11».
 */
public record Icd10CodeResponse(
        String code,
        String name,
        String parent,
        boolean isRubric
) {
}
