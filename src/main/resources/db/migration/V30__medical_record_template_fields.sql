-- Расширение medical_record до полного шаблона амбулаторного приёма
-- (форма «Дневниковая запись консультации» по приказу Минздрава России №834н).
-- Существующие 5 полей (title / complaints / diagnosis / recommendations / prescriptions)
-- остаются без изменений, добавляем недостающие секции протокола приёма.
ALTER TABLE medical_record
    ADD COLUMN record_anamnesis_morbi   TEXT,
    ADD COLUMN record_anamnesis_vitae   TEXT,
    ADD COLUMN record_objective_status  TEXT,
    ADD COLUMN record_local_status      TEXT,
    ADD COLUMN record_examination_plan  TEXT,
    ADD COLUMN record_icd10_code        VARCHAR(20),
    ADD COLUMN record_next_visit_date   DATE;
