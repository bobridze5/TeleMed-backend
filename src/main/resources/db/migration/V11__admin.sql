CREATE TABLE admins (
    admin_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_admin_user FOREIGN KEY (admin_id) REFERENCES users (user_id) ON DELETE CASCADE
);