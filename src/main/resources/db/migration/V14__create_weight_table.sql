CREATE TABLE weight (
    weight_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    weight_value DOUBLE PRECISION NOT NULL,
    weight_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    weight_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_weight_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_weight_patient_id ON weight(patient_id);
