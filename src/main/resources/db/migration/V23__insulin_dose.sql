CREATE TABLE insulin_dose (
    insulin_dose_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    meal_id BIGINT,
    insulin_dose_units DOUBLE PRECISION NOT NULL,
    insulin_dose_type VARCHAR(20) NOT NULL,
    insulin_dose_note TEXT,
    insulin_dose_taken_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    insulin_dose_created_at TIMESTAMP WITHOUT TIME ZONE,
    insulin_dose_updated_at TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_insulin_dose_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_insulin_dose_meal
        FOREIGN KEY (meal_id)
        REFERENCES meal(meal_id)
        ON DELETE SET NULL,

    CONSTRAINT ck_insulin_dose_units_positive CHECK (insulin_dose_units > 0),

    CONSTRAINT ck_insulin_dose_type CHECK (insulin_dose_type IN ('SHORT', 'LONG', 'MIX'))
);

CREATE INDEX idx_insulin_dose_patient_taken_at
    ON insulin_dose(patient_id, insulin_dose_taken_at DESC);

CREATE INDEX idx_insulin_dose_meal_id
    ON insulin_dose(meal_id);
