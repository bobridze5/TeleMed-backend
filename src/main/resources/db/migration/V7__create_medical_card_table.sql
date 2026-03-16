CREATE TABLE "medical_card" (
    card_id BIGSERIAL PRIMARY KEY,
    record_id BIGINT REFERENCES medical_record(record_id),
    patient_id BIGINT REFERENCES patients(patient_id)
)