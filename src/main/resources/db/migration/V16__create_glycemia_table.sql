CREATE TABLE glycemia (
    glycemia_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    glycemia_level DOUBLE PRECISION NOT NULL,
    glycemia_type VARCHAR(100) NOT NULL,
    glycemia_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    glycemia_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_glycemia_records_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_glycemia_patient_id ON glycemia(patient_id);
