CREATE TABLE weight (
    weight_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    weight_value DOUBLE PRECISION NOT NULL,
    weight_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    weight_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_weight_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_weight_patient_id ON weight (patient_id);

CREATE TABLE blood_pressures (
    blood_pressure_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    blood_pressure_systolic INTEGER NOT NULL,
    blood_pressure_diastolic INTEGER NOT NULL,
    blood_pressure_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    blood_pressure_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_blood_pressures_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_blood_pressures_patient_id ON blood_pressures (patient_id);

CREATE TABLE physical_activities (
    physical_activity_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    physical_activity_type VARCHAR(100) NOT NULL,
    physical_activity_duration INTEGER NOT NULL,
    physical_activity_intensity VARCHAR(50) NOT NULL,
    physical_activity_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    physical_activity_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_physical_activities_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_physical_activities_patient_id ON physical_activities (patient_id);

CREATE TABLE symptoms (
    symptom_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    symptom_severity VARCHAR(50) NOT NULL,
    symptom_description TEXT,
    symptom_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    symptom_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_symptoms_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_symptoms_patient_id ON symptoms (patient_id);

CREATE TABLE glycemia (
    glycemia_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    glycemia_level DOUBLE PRECISION NOT NULL,
    glycemia_type VARCHAR(100) NOT NULL,
    glycemia_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    glycemia_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_glycemia_records_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_glycemia_patient_id ON glycemia (patient_id);