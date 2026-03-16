CREATE TABLE doctors (
    doctor_id BIGSERIAL PRIMARY KEY,
    doctor_specialization VARCHAR(120) NOT NULL,
    doctor_experience INTEGER NOT NULL,
    doctor_qualification VARCHAR(120) NOT NULL,
    doctor_user_id BIGINT REFERENCES users(user_id),
    doctor_organization BIGINT REFERENCES medical_organizations(organization_id)
)