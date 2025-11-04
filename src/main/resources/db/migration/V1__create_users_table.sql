CREATE TABLE users (
    users_id BIGSERIAL PRIMARY KEY,
    users_first_name VARCHAR(120),
    users_last_name VARCHAR(120),
    users_middle_name VARCHAR(120),
    users_username VARCHAR(60) NOT NULL UNIQUE,
    users_nickname VARCHAR(60) NOT NULL UNIQUE,
    users_email VARCHAR(255) NOT NULL UNIQUE,
    users_password_hash VARCHAR(255) NOT NULL,
    users_status VARCHAR(50) NOT NULL,
    users_datetime_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    users_datetime_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);