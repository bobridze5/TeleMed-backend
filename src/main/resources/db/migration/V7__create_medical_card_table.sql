CREATE TABLE "medical_card" (
    card_id BIGSERIAL PRIMARY KEY,
    record_id INTEGER REFERENCES medical_record(record_id),
    patient_id INTEGER REFERENCES patients(patient_id)
)