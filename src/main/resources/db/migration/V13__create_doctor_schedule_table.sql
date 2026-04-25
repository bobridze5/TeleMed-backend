CREATE TABLE doctor_schedules (
    schedule_id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    schedule_day_of_week VARCHAR(10) NOT NULL,
    schedule_start_time TIME NOT NULL,
    schedule_end_time TIME NOT NULL,
    schedule_slot_duration_minutes INT NOT NULL,

    CONSTRAINT fk_schedule_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    CONSTRAINT uq_schedule_doctor_day UNIQUE (doctor_id, schedule_day_of_week),
    CONSTRAINT chk_schedule_times CHECK (schedule_end_time > schedule_start_time),
    CONSTRAINT chk_slot_duration CHECK (schedule_slot_duration_minutes BETWEEN 5 AND 240)
);

CREATE INDEX idx_schedule_doctor ON doctor_schedules(doctor_id);
