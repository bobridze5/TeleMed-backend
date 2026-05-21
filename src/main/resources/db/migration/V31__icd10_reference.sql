CREATE TABLE icd10_codes (
    icd10_code VARCHAR(10) PRIMARY KEY,
    icd10_name TEXT NOT NULL,
    icd10_parent VARCHAR(10),
    icd10_is_rubric BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_icd10_parent ON icd10_codes (icd10_parent);

CREATE TABLE medical_record_icd10_codes (
    record_id BIGINT NOT NULL,
    icd10_code VARCHAR(20) NOT NULL,
    PRIMARY KEY (record_id, icd10_code),
    CONSTRAINT fk_mricd10_record FOREIGN KEY (record_id) REFERENCES medical_record (record_id) ON DELETE CASCADE
);

CREATE INDEX idx_mricd10_code ON medical_record_icd10_codes (icd10_code);