package com.bobridze5.TeleMed_backend.api.dto.test;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TestFindRiscRequest(
        @NotNull @PositiveOrZero Integer agePoints,
        @NotNull @PositiveOrZero Integer bodyMassIndexPoints,
        @NotNull @PositiveOrZero Integer waistCircumferencePoints,
        @NotNull @PositiveOrZero Integer vegetablePoints,
        @NotNull @PositiveOrZero Integer activityPoints,
        @NotNull @PositiveOrZero Integer hypertensionPoints,
        @NotNull @PositiveOrZero Integer glucosePoints,
        @NotNull @PositiveOrZero Integer familyHistoryPoints
) {
}
