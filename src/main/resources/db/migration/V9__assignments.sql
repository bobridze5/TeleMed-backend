CREATE TABLE patient_doctor_assignments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    assigned_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

CONSTRAINT uq_patient_doctor UNIQUE (patient_id, doctor_id),

CONSTRAINT fk_patient FOREIGN KEY (patient_id)
        REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors (doctor_id) ON DELETE CASCADE
);

CREATE INDEX idx_assignment_patient ON patient_doctor_assignments (patient_id);

CREATE INDEX idx_assignment_doctor ON patient_doctor_assignments (doctor_id);