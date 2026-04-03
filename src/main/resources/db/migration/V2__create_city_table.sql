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
