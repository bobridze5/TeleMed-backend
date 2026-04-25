-- V23: Рефакторинг schedule_slots - из @ElementCollection в отдельную @Entity таблицу
-- Добавляем собственный ID, timestamp, улучшаем структуру

-- Создать новую таблицу с собственным ID
CREATE TABLE schedule_slots (
    slot_id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    consultation_type VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_slot_schedule
        FOREIGN KEY (schedule_id)
        REFERENCES doctor_schedules(schedule_id)
        ON DELETE CASCADE,

    -- Уникальность: один слот на расписание
    CONSTRAINT uk_schedule_slot_time
        UNIQUE (schedule_id, start_time, end_time, consultation_type)
);

CREATE INDEX idx_schedule_slots_schedule_id ON schedule_slots(schedule_id);
CREATE INDEX idx_schedule_slots_start_time ON schedule_slots(start_time);
