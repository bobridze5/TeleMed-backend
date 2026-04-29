CREATE TABLE notification (
    notification_id BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    reminder_id BIGINT,

    notification_title   VARCHAR(200) NOT NULL,
    notification_message VARCHAR(1000),
    notification_type    VARCHAR(40),
    notification_is_read BOOLEAN NOT NULL DEFAULT FALSE,
    notification_created_at TIMESTAMPTZ,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,

    CONSTRAINT fk_notification_reminder
        FOREIGN KEY (reminder_id) REFERENCES reminder(reminder_id) ON DELETE SET NULL
);

CREATE INDEX idx_notification_user_created
    ON notification(user_id, notification_created_at DESC);
CREATE INDEX idx_notification_user_unread
    ON notification(user_id, notification_is_read);
