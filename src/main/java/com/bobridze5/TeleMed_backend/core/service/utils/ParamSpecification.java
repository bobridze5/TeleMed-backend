package com.bobridze5.TeleMed_backend.core.service.utils;

import com.bobridze5.TeleMed_backend.api.dto.params.DateFilter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class ParamSpecification {

    public static <T> Specification<T> hasPatient(Long patientId) {
        return ((root, query, cb) ->
                cb.equal(root.get("patient").get("id"), patientId)
        );
    }

    public static <T> Specification<T> isBetween(DateFilter filter) {
        return (root, query, cb) -> {
            if (filter == null || !filter.isBetween()) {
                return null;
            }

            return cb.between(root.get("createdAt"), filter.startDate(), filter.endDate());
        };
    }

    public static <T> Specification<T> byPatientAndDateTimeBetween(
            Long patientId,
            DateFilter filter
    ) {
        return Specification.<T>unrestricted().and(hasPatient(patientId)).and(isBetween(filter));
    }

}
