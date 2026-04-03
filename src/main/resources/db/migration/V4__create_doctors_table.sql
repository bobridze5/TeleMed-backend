CREATE TABLE specializations (
    specialization_id BIGSERIAL PRIMARY KEY,
    specialization_name VARCHAR(128) NOT NULL UNIQUE,
    specialization_description VARCHAR(500)
);

CREATE TABLE doctors (
    doctor_id BIGINT PRIMARY KEY,
    doctor_specialization_id BIGINT NOT NULL REFERENCES specializations(specialization_id),
    doctor_experience INTEGER NOT NULL CHECK (doctor_experience >= 0),
    doctor_qualification VARCHAR(120) NOT NULL,
    doctor_user_id BIGINT NOT NULL UNIQUE REFERENCES users(user_id),
    doctor_organization_id BIGINT REFERENCES medical_organizations(organization_id),

    CONSTRAINT fk_doctor_user FOREIGN KEY (doctor_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_doctors_specialization ON doctors(doctor_specialization_id);

INSERT INTO specializations (specialization_name, specialization_description) VALUES
('Эндокринолог', 'Специалист по диагностике и лечению заболеваний эндокринной системы.'),
('Диабетолог', 'Врач, специализирующийся именно на лечении сахарного диабета и его осложнений.'),
('Подолог', 'Специалист по уходу за стопой, критически важный при профилактике "диабетической стопы".'),
('Диетолог', 'Помогает составить индивидуальный план питания для контроля уровня сахара.');
