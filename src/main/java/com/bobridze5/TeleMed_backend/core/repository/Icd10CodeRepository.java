package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.Icd10Code;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Icd10CodeRepository extends JpaRepository<Icd10Code, String> {

    /**
     * Поиск по коду или названию (case-insensitive substring).
     * Сортируем сначала по точному совпадению кода (LIKE 'E11%'),
     * затем по алфавитному коду — чтобы при наборе «E11» сначала
     * выпадали E11/E11.0/E11.1 …, а не «другие формы СД».
     */
    @Query("""
        SELECT c FROM Icd10Code c
        WHERE LOWER(c.code) LIKE LOWER(CONCAT(:q, '%'))
           OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY
            CASE WHEN LOWER(c.code) LIKE LOWER(CONCAT(:q, '%')) THEN 0 ELSE 1 END,
            c.code
        """)
    List<Icd10Code> search(@Param("q") String query, Pageable pageable);
}
