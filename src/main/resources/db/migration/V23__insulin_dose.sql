CREATE TABLE insulin_dose (
    insulin_dose_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    insulin_dose_units DOUBLE PRECISION NOT NULL,
    insulin_dose_type VARCHAR(20) NOT NULL,
    insulin_dose_note TEXT,
    insulin_dose_taken_at TIMESTAMPTZ NOT NULL,
    insulin_dose_created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    insulin_dose_updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_insulin_dose_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT ck_insulin_dose_units_positive CHECK (insulin_dose_units > 0),
    CONSTRAINT ck_insulin_dose_type CHECK (insulin_dose_type IN ('SHORT', 'LONG', 'MIX'))
);

CREATE INDEX idx_insulin_dose_patient_taken_at ON insulin_dose (
    patient_id,
    insulin_dose_taken_at DESC
);