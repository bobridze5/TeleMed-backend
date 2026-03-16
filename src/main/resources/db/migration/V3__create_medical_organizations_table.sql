CREATE TABLE medical_organizations (
    organization_id BIGSERIAL PRIMARY KEY,
    organization_name VARCHAR(120) NOT NULL,
    organization_address VARCHAR(120) NOT NULL,
    organization_email VARCHAR(60) UNIQUE NOT NULL,
    organization_phone VARCHAR(60) UNIQUE NOT NULL,
    organization_city BIGINT REFERENCES cities(city_id)
);

CREATE INDEX idx_organizations_email ON medical_organizations(organization_email);
CREATE INDEX idx_organizations_phone ON medical_organizations(organization_phone);