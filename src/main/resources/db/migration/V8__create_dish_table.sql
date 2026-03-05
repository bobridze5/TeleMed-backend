CREATE TABLE "dish" (
    dish_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    dish_name VARCHAR(255) NOT NULL,
    dish_calories_per_100 DOUBLE PRECISION NOT NULL,
    dish_carbs_per_100 DOUBLE PRECISION NOT NULL,
    dish_protein_per_100 DOUBLE PRECISION NOT NULL,
    dish_fats_per_100 DOUBLE PRECISION NOT NULL,
    dish_photo_url VARCHAR(512),
    dish_description TEXT,
    dish_status VARCHAR(50) NOT NULL,

    -- FK на таблицу пациентов
    CONSTRAINT fk_dish_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients (patient_id)
ON DELETE SET NULL,

    -- Твои проверки @Check
    CONSTRAINT dish_values_positive CHECK (
        dish_calories_per_100 >= 0 AND
        dish_carbs_per_100 >= 0 AND
        dish_protein_per_100 >= 0 AND
        dish_fats_per_100 >= 0
    )
);

-- Индекс для оптимизации выборок по пациенту
CREATE INDEX idx_dish_patient_id ON dish(patient_id);