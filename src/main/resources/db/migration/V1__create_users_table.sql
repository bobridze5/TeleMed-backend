CREATE TABLE users (
    users_id BIGSERIAL PRIMARY KEY,
    users_first_name VARCHAR(120),
    users_last_name VARCHAR(120),
    users_middle_name VARCHAR(120),
    users_email VARCHAR(255) UNIQUE NOT NULL,
    users_password_hash VARCHAR(255) NOT NULL,
    users_status VARCHAR(50) NOT NULL,
    users_datetime_created TIMESTAMP NOT NULL DEFAULT NOW(),
    users_datetime_updated TIMESTAMP NOT NULL DEFAULT NOW()
);