CREATE TABLE glycemia_records (
    glycemia_report_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    glycemia_report_level DOUBLE PRECISION NOT NULL,
    glycemia_report_type VARCHAR(100) NOT NULL,
    glycemia_report_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_glycemia_records_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_glycemia_records_patient_id ON glycemia_records(patient_id);
