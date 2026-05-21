CREATE TYPE consultation_type AS ENUM ('VIDEO', 'AUDIO', 'CHAT');

CREATE TYPE appointment_status AS ENUM ('CREATED', 'CONFIRMED', 'CANCELED', 'COMPLETED', 'NO_SHOW');

CREATE TABLE appointments (
    appointment_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    appointment_consultation_type VARCHAR(50) NOT NULL,
    appointment_status VARCHAR(50) NOT NULL,
    appointment_link VARCHAR(255),
    appointment_reason TEXT,
    appointment_confirmed_by_patient BOOLEAN NOT NULL DEFAULT FALSE,
    appointment_confirmed_by_doctor BOOLEAN NOT NULL DEFAULT FALSE,
    appointment_meeting_phone VARCHAR(64),
    appointment_meeting_notes TEXT,
    appointment_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    appointment_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id) ON DELETE CASCADE
);

CREATE INDEX idx_appointment_doctor_date ON appointments (
    doctor_id,
    appointment_datetime
);

CREATE INDEX idx_appointment_patient_date ON appointments (
    patient_id,
    appointment_datetime
);