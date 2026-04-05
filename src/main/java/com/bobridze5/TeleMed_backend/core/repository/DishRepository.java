package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
//    @Query
//    Page<Dish> findAllFiltered(Long patientId,
//                               String name,
//                               Double minCal, Double maxCal,
//                               Double minProt, Double maxProt,
//                               Double minFat, Double maxFat,
//                               Double minCarb, Double maxCarb,
//                               Pageable pageable);
}
