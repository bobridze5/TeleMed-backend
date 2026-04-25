-- Старая таблица medical_card была обёрткой над одной записью; убираем её.
-- Теперь "медкарта" — это агрегат (профиль + аллергии + препараты + события + записи врачей),
-- собираемый на уровне DTO, а medical_record хранит записи врачей с полным набором полей.

DROP TABLE IF EXISTS medical_card;
DROP TABLE IF EXISTS medical_record;

CREATE TABLE medical_record (
    record_id          BIGSERIAL PRIMARY KEY,
    patient_id         BIGINT       NOT NULL,
    doctor_id          BIGINT       NOT NULL,
    appointment_id     BIGINT,
    record_title       VARCHAR(255) NOT NULL,
    record_complaints  TEXT,
    record_diagnosis   TEXT,
    record_recommendations TEXT,
    record_prescriptions   TEXT,
    record_created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    record_updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_record_patient     FOREIGN KEY (patient_id)     REFERENCES patients(patient_id)     ON DELETE CASCADE,
    CONSTRAINT fk_record_doctor      FOREIGN KEY (doctor_id)      REFERENCES doctors(doctor_id)       ON DELETE RESTRICT,
    CONSTRAINT fk_record_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL
);

CREATE INDEX idx_medical_record_patient ON medical_record(patient_id, record_created_at DESC);
CREATE INDEX idx_medical_record_doctor  ON medical_record(doctor_id);
