package com.bobridze5.TeleMed_backend.api.dto.test;

import java.time.LocalDateTime;

public record TestFindRiscResponse(
        Long testId,
        Integer agePoints,
        Integer bodyMassIndexPoints,
        Integer waistCircumferencePoints,
        Integer vegetablePoints,
        Integer activityPoints,
        Integer hypertensionPoints,
        Integer glucosePoints,
        Integer familyHistoryPoints,
        Integer totalPoints,
        LocalDateTime createdAt
) {
}
