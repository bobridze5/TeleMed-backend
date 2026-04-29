-- Двухстороннее подтверждение записи: отдельные флаги для пациента и врача,
-- плюс детали встречи (телефон/заметка/пароль), которые задаёт врач при
-- подтверждении.

ALTER TABLE appointments
    ADD COLUMN appointment_confirmed_by_patient BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN appointment_confirmed_by_doctor  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN appointment_meeting_phone        VARCHAR(64),
    ADD COLUMN appointment_meeting_notes        TEXT;

-- Backfill: если запись уже была переведена в CONFIRMED по старой логике
-- (одно подтверждение трактовалось как «подтверждено»), считаем, что обе
-- стороны подтвердили. Колонка appointment_confirmed_by ('PATIENT'/'DOCTOR')
-- нам тут больше не нужна, но оставляем её для совместимости.
UPDATE appointments
SET appointment_confirmed_by_patient = TRUE,
    appointment_confirmed_by_doctor  = TRUE
WHERE appointment_status = 'CONFIRMED';

-- Если запись была подтверждена только пациентом, а статус остался CREATED
-- (новая логика так делать ещё не умеет, но на всякий случай) — не трогаем.
