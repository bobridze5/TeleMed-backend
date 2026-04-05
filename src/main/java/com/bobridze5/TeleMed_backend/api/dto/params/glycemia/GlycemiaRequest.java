package com.bobridze5.TeleMed_backend.api.dto.params.glycemia;

import com.bobridze5.TeleMed_backend.core.entity.report.GlycemiaType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GlycemiaRequest(
        @NotNull(message = "Уровень гликемии обязателен")
        @Positive(message = "Уровень гликемии не может быть отрицательным")
        Double level,

        @NotNull(message = "Тип должен быть указан")
        GlycemiaType type
) {
}
