CREATE TABLE patients (
    patient_id BIGINT PRIMARY KEY,

    patient_diabetes_type VARCHAR(50),
    patient_diagnosis_date DATE,

    patient_insulin_dependency BOOLEAN NOT NULL DEFAULT FALSE,
    patient_target_low DOUBLE PRECISION NOT NULL DEFAULT 3.9,
    patient_target_high DOUBLE PRECISION NOT NULL DEFAULT 10.0,

    CONSTRAINT fk_patients_on_user FOREIGN KEY (patient_id) REFERENCES users (user_id) ON DELETE CASCADE
);
