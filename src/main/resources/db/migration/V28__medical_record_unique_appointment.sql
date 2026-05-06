-- Одна запись консультации на приём.
--
-- Используем partial unique index, потому что у нас есть и записи без
-- привязки к приёму (appointment_id IS NULL — например, врач создал запись
-- вне контекста конкретного приёма): для них уникальности не требуется,
-- иначе несколько NULL-записей конфликтовали бы между собой (хотя в
-- PostgreSQL NULL-значения и не считаются равными в UNIQUE, явный partial
-- index делает намерение более очевидным).
--
-- На случай существующих дублей оставляем самую свежую запись и удаляем
-- более ранние — таким образом миграция не упадёт на проде, а доктор
-- сможет дальше управлять единственной актуальной записью.

DELETE FROM medical_record
WHERE record_id IN (
    SELECT mr.record_id
    FROM medical_record mr
    JOIN (
        SELECT appointment_id, MAX(record_id) AS keep_id
        FROM medical_record
        WHERE appointment_id IS NOT NULL
        GROUP BY appointment_id
        HAVING COUNT(*) > 1
    ) keepers ON mr.appointment_id = keepers.appointment_id AND mr.record_id <> keepers.keep_id
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_medical_record_appointment
    ON medical_record (appointment_id)
    WHERE appointment_id IS NOT NULL;
