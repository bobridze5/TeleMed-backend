-- Поддержка ручного задания слотов (без авторасчёта из диапазона).
-- Если для расписания есть записи в doctor_schedule_slots, они имеют приоритет над range-полями.

CREATE TABLE doctor_schedule_slots (
    schedule_id BIGINT NOT NULL,
    slot_time TIME NOT NULL,
    PRIMARY KEY (schedule_id, slot_time),
    CONSTRAINT fk_schedule_slot FOREIGN KEY (schedule_id)
        REFERENCES doctor_schedules(schedule_id) ON DELETE CASCADE
);

CREATE INDEX idx_schedule_slots_schedule ON doctor_schedule_slots(schedule_id);
