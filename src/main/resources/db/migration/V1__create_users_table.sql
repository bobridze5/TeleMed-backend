CREATE TABLE "users" (
    user_id BIGSERIAL PRIMARY KEY,
    user_first_name VARCHAR(120),
    user_last_name VARCHAR(120),
    user_middle_name VARCHAR(120),
--    user_username VARCHAR(60) NOT NULL UNIQUE,
--    user_nickname VARCHAR(60) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    user_password_hash VARCHAR(255) NOT NULL,
    user_status VARCHAR(50) NOT NULL,
    user_datetime_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_datetime_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_users_email ON users(user_email);