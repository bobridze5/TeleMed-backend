CREATE TABLE blood_pressures (
    blood_pressure_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    blood_pressure_systolic INTEGER NOT NULL,
    blood_pressure_diastolic INTEGER NOT NULL,
    blood_pressure_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_blood_pressures_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_blood_pressures_patient_id ON blood_pressures(patient_id);
