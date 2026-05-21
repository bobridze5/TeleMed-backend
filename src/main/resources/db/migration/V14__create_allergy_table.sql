CREATE TABLE allergies (
    allergy_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    allergen VARCHAR(255) NOT NULL,
    reaction VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_allergy_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_allergies_patient_id ON allergies (patient_id);