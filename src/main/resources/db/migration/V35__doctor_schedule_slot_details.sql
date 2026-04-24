-- Расширяем doctor_schedule_slots: слот теперь имеет интервал (start/end)
-- и тип консультации (VIDEO/AUDIO/CHAT).

DROP TABLE IF EXISTS doctor_schedule_slots;

CREATE TABLE doctor_schedule_slots (
    schedule_id       BIGINT       NOT NULL,
    start_time        TIME         NOT NULL,
    end_time          TIME         NOT NULL,
    consultation_type VARCHAR(16)  NOT NULL,
    PRIMARY KEY (schedule_id, start_time, consultation_type),
    CONSTRAINT fk_schedule_slot FOREIGN KEY (schedule_id)
        REFERENCES doctor_schedules(schedule_id) ON DELETE CASCADE
);

CREATE INDEX idx_schedule_slots_schedule ON doctor_schedule_slots(schedule_id);
