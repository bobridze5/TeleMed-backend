package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.*;

/**
 * Справочник кодов МКБ-10 (Международная классификация болезней, 10-я редакция).
 * Заполняется миграцией {@code V32__icd10_seed.sql} — курированный набор
 * под аудиторию приложения (диабет + сопутствующие диагнозы).
 *
 * Read-only с точки зрения приложения: записывается только Flyway-сидом,
 * через API не редактируется. Используется как источник для autocomplete
 * на фронте (выбор кодов в записи о приёме врача).
 */
@Entity
@Table(name = "icd10_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Icd10Code {
    @Id
    @Column(name = "icd10_code", length = 10)
    private String code;

    @Column(name = "icd10_name", nullable = false, columnDefinition = "TEXT")
    private String name;

    /** Код вышестоящей рубрики (например, для E11.9 → E11). Может быть null. */
    @Column(name = "icd10_parent", length = 10)
    private String parent;

    /** true для рубрики верхнего уровня (E11, I10, …) — у них нет подкодов. */
    @Column(name = "icd10_is_rubric", nullable = false)
    private Boolean isRubric;
}
