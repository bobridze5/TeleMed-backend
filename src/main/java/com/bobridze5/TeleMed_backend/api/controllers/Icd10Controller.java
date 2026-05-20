package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.medcard.Icd10CodeResponse;
import com.bobridze5.TeleMed_backend.core.service.medcard.Icd10Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Поиск по справочнику МКБ-10. Открыт всем аутентифицированным пользователям —
 * это публичный медицинский справочник без чувствительных данных.
 * Используется фронтом-врачом для autocomplete при заполнении записи о приёме.
 */
@RestController
@RequestMapping(API.ICD10)
@RequiredArgsConstructor
@Tag(name = "Справочник МКБ-10", description = "Поиск по кодам и названиям диагнозов")
public class Icd10Controller {
    private final Icd10Service icd10Service;

    @GetMapping("/search")
    @Operation(summary = "Поиск по коду или названию (case-insensitive)")
    public List<Icd10CodeResponse> search(
            @RequestParam(name = "q", required = false, defaultValue = "") String query,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit
    ) {
        return icd10Service.search(query, limit);
    }
}
