CREATE TABLE medical_events (
    event_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    event_date DATE NOT NULL,
    end_date DATE,
    event_type VARCHAR(100),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_medical_event_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_medical_events_patient_id ON medical_events (patient_id);