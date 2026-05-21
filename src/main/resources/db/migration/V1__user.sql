CREATE TABLE "users" (
    user_id BIGSERIAL PRIMARY KEY,
    user_first_name VARCHAR(120),
    user_last_name VARCHAR(120),
    user_middle_name VARCHAR(120),
    user_date_of_birth DATE,
    user_gender CHAR(1),
    user_email VARCHAR(255) NOT NULL UNIQUE,
    user_password_hash VARCHAR(255) NOT NULL,
    user_status VARCHAR(50) NOT NULL,
    user_datetime_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_datetime_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_time_zone VARCHAR(50) NOT NULL DEFAULT 'Europe/Moscow'
);

CREATE INDEX idx_users_email ON users (user_email);