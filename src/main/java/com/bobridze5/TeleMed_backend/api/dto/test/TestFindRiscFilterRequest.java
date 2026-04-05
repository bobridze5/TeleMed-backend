package com.bobridze5.TeleMed_backend.api.dto.test;

import com.bobridze5.TeleMed_backend.api.dto.params.DateFilter;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record TestFindRiscFilterRequest(
        String sortBy,
        String sortDirection,
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) implements DateFilter {
    
    public Sort.Direction getSortDirection() {
        if (sortDirection == null || sortDirection.isBlank()) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.valueOf(sortDirection.toUpperCase());
    }

    public String getSortField() {
        if (sortBy == null) return "createdAt";
        return switch (sortBy) {
            case "age" -> "agePoints";
            case "bmi" -> "bodyMassIndexPoints";
            case "waist" -> "waistCircumferencePoints";
            case "vegetable" -> "vegetablePoints";
            case "activity" -> "activityPoints";
            case "hypertension" -> "hypertensionPoints";
            case "glucose" -> "glucosePoints";
            case "history" -> "familyHistoryPoints";
            case "total" -> "totalPoints";
            default -> "createdAt";
        };
    }
}
