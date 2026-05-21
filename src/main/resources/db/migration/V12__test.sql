CREATE TABLE test_results (
    test_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    test_points_age INTEGER NOT NULL,
    test_points_bmi INTEGER NOT NULL,
    test_points_waist INTEGER NOT NULL,
    test_points_vegetable INTEGER NOT NULL,
    test_points_activity INTEGER NOT NULL,
    test_points_hypertension INTEGER NOT NULL,
    test_points_glucose INTEGER NOT NULL,
    test_points_history INTEGER NOT NULL,
    test_points_total INTEGER NOT NULL,
    test_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_test_results_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE INDEX idx_test_results_patient_id ON test_results (patient_id);