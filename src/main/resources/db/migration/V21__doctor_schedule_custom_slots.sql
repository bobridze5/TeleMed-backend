CREATE TABLE doctor_schedules (
    schedule_id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    schedule_day_of_week VARCHAR(20) NOT NULL,

    -- Ограничение уникальности (один врач - одно расписание на конкретный день недели)
    CONSTRAINT uk_doctor_day UNIQUE (doctor_id, schedule_day_of_week),

    -- Внешний ключ на таблицу докторов (убедитесь, что имя таблицы doctor корректно)
    CONSTRAINT fk_doctor_schedule FOREIGN KEY (doctor_id)
        REFERENCES doctors (doctor_id) ON DELETE CASCADE
);

-- Индекс для ускорения поиска расписания конкретного врача
CREATE INDEX idx_doctor_schedules_doctor_id ON doctor_schedules(doctor_id);
