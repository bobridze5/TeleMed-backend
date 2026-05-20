-- Справочник МКБ-10 + переход medical_record на список кодов вместо одного.
-- Врач выбирает коды из справочника (autocomplete), а не набирает вручную.

-- 1. Справочник кодов МКБ-10
CREATE TABLE icd10_codes (
    icd10_code    VARCHAR(10)  PRIMARY KEY,
    icd10_name    TEXT         NOT NULL,
    icd10_parent  VARCHAR(10),
    icd10_is_rubric BOOLEAN    NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_icd10_parent ON icd10_codes(icd10_parent);
-- ILIKE-поиск по названию — без русскоязычного полнотекстового поиска
-- достаточно trigram/индекса на LOWER(name), но для MVP хватает seq scan
-- по 200 строкам. Если вырастем до всего МКБ-10, добавим pg_trgm.

-- 2. Связь запись ↔ коды (M:N через массив-таблицу, это соответствует
-- @ElementCollection на стороне JPA — проще чем отдельная entity).
-- Код может не быть в справочнике (врач вписал что-то своё) — FK на
-- icd10_codes не ставим, храним как свободную строку.
CREATE TABLE medical_record_icd10_codes (
    record_id  BIGINT      NOT NULL,
    icd10_code VARCHAR(20) NOT NULL,
    PRIMARY KEY (record_id, icd10_code),
    CONSTRAINT fk_mricd10_record FOREIGN KEY (record_id)
        REFERENCES medical_record(record_id) ON DELETE CASCADE
);

CREATE INDEX idx_mricd10_code ON medical_record_icd10_codes(icd10_code);

-- 3. Сносим старое single-поле из V30 — теперь коды живут в join-таблице.
ALTER TABLE medical_record DROP COLUMN record_icd10_code;
