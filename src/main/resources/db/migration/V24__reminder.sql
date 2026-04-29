CREATE TABLE reminder (
    reminder_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,

    reminder_kind VARCHAR(20) NOT NULL,

    reminder_title VARCHAR(200) NOT NULL,
    reminder_message VARCHAR(1000),

    reminder_scheduled_at TIMESTAMPTZ,

    reminder_recurrence_time TIME,
    reminder_recurrence_days VARCHAR(40),

    reminder_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    reminder_last_fired_at TIMESTAMPTZ,

    reminder_created_at TIMESTAMPTZ,
    reminder_updated_at TIMESTAMPTZ,

    CONSTRAINT fk_reminder_patient
        FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,

    CONSTRAINT ck_reminder_kind
        CHECK (reminder_kind IN ('ONE_SHOT', 'RECURRING')),

    CONSTRAINT ck_reminder_kind_fields
        CHECK (
            (reminder_kind = 'ONE_SHOT'  AND reminder_scheduled_at IS NOT NULL) OR
            (reminder_kind = 'RECURRING' AND reminder_recurrence_time IS NOT NULL AND reminder_recurrence_days IS NOT NULL)
        )
);

CREATE INDEX idx_reminder_patient_enabled ON reminder(patient_id, reminder_enabled);
CREATE INDEX idx_reminder_kind_enabled    ON reminder(reminder_kind, reminder_enabled);
