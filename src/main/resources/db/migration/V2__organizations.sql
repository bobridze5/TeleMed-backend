CREATE TABLE regions (
    region_id BIGSERIAL PRIMARY KEY,
    region_name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE cities (
    city_id BIGSERIAL PRIMARY KEY,
    city_name VARCHAR(60) NOT NULL,
    region_id BIGINT NOT NULL REFERENCES regions(region_id),
    CONSTRAINT uk_city_region UNIQUE (city_name, region_id)
);

CREATE INDEX idx_cities_region_id ON cities(region_id);


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