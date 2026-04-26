CREATE TABLE reviews (
    review_id      BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(appointment_id) ON DELETE CASCADE,
    patient_id     BIGINT NOT NULL REFERENCES patients(patient_id),
    doctor_id      BIGINT NOT NULL REFERENCES doctors(doctor_id),
    rating         SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment        TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (appointment_id)
);
