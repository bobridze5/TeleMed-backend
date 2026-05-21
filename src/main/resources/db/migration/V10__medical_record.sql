CREATE TABLE medical_record (
    record_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT,
    record_title VARCHAR(255) NOT NULL,
    record_complaints TEXT,
    record_diagnosis TEXT,
    record_recommendations TEXT,
    record_prescriptions TEXT,
    record_anamnesis_morbi TEXT,
    record_anamnesis_vitae TEXT,
    record_objective_status TEXT,
    record_local_status TEXT,
    record_examination_plan TEXT,
    record_next_visit_date DATE,
    record_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    record_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_record_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id) ON DELETE RESTRICT,
    CONSTRAINT fk_record_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id) ON DELETE SET NULL
);

CREATE INDEX idx_medical_record_patient ON medical_record (
    patient_id,
    record_created_at DESC
);

CREATE INDEX idx_medical_record_doctor ON medical_record (doctor_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_medical_record_appointment ON medical_record (appointment_id)
WHERE
    appointment_id IS NOT NULL;