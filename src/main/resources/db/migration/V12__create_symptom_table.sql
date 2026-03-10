CREATE TABLE symptoms (
    symptom_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    symptom_severity VARCHAR(50) NOT NULL,
    symptom_description TEXT,
    symptom_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_symptoms_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_symptoms_patient_id ON symptoms(patient_id);