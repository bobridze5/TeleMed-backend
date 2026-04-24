CREATE TABLE "meal" (
    meal_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    meal_datetime TIMESTAMP WITHOUT TIME ZONE,
    meal_type VARCHAR(50) NOT NULL,

    CONSTRAINT check_meal_type CHECK (meal_type IN ('BREAKFAST', 'BRUNCH', 'LUNCH', 'DINNER', 'SUPPER', 'SNACK')),

    CONSTRAINT fk_meal_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients (patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_meal_patient_id ON meal(patient_id);