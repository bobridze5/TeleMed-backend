CREATE TABLE physical_activities (
    physical_activity_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    physical_activity_type VARCHAR(100) NOT NULL,
    physical_activity_duration INTEGER NOT NULL,
    physical_activity_intensity VARCHAR(50) NOT NULL,
    physical_activity_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_physical_activities_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_physical_activities_patient_id ON physical_activities(patient_id);
