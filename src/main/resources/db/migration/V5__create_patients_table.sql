CREATE TABLE "patients" (
    patient_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE, -- UNIQUE обязателен для OneToOne
    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
