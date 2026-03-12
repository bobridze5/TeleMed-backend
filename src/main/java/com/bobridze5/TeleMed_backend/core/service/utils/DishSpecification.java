package com.bobridze5.TeleMed_backend.core.service.utils;


import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class DishSpecification {
    public static Specification<Dish> build(DishFilterRequest request, Long patientId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            Predicate ownDishes = cb.equal(root.get("patient").get("id"), patientId);
            Predicate publicDishes = cb.isNull(root.get("patient"));

            predicates.add(cb.or(ownDishes, publicDishes));

            // 1. Поиск по названию (LIKE %query%)
            if (hasText(request.name())) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.name().toLowerCase() + "%"));
            }

            // 2. Диапазоны КБЖУ (Калории, Белки, Жиры, Углеводы)
            addRangePredicate(predicates, cb, root.get("calories"), request.minCal(), request.maxCal());
            addRangePredicate(predicates, cb, root.get("protein"), request.minProt(), request.maxProt());
            addRangePredicate(predicates, cb, root.get("fats"), request.minFat(), request.maxFat());
            addRangePredicate(predicates, cb, root.get("carbs"), request.minCarb(), request.maxCarb());

            // 3. Фильтр по статусу
            if (request.status() != null) {
                predicates.add(cb.equal(root.get("status"), request.status()));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addRangePredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                          Path<Double> path, Double min, Double max) {
        if (min != null) predicates.add(cb.ge(path, min));
        if (max != null) predicates.add(cb.le(path, max));
    }

    private static boolean hasText(String str) {
        return str != null && !str.isBlank();
    }
}
