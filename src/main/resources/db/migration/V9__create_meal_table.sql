CREATE TABLE "meal" (
    meal_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    meal_time TIME WITHOUT TIME ZONE,
    meal_type VARCHAR(50) NOT NULL,

    -- Ограничение для Enum MealType
    CONSTRAINT check_meal_type CHECK (meal_type IN ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK')),

    -- Внешний ключ на пациента
    CONSTRAINT fk_meal_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients (patient_id)
        ON DELETE CASCADE
);

-- Индекс для быстрого поиска приемов пищи конкретного пациента
CREATE INDEX idx_meal_patient_id ON meal(patient_id);