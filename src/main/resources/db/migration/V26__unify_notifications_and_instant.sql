-- Перенос данных из doctor_notifications в unified notification.
-- Doctor extends User (JOINED), у них одинаковый id → можно копировать doctor_id напрямую как user_id.
INSERT INTO notification (user_id, notification_title, notification_message, notification_type, notification_is_read, notification_created_at)
SELECT doctor_id,
       title,
       message,
       'DOCTOR_APPROVAL',
       read,
       created_at AT TIME ZONE 'Europe/Moscow'
FROM doctor_notifications;

DROP TABLE doctor_notifications;

ALTER TABLE insulin_dose
    ALTER COLUMN insulin_dose_taken_at   TYPE TIMESTAMPTZ USING insulin_dose_taken_at   AT TIME ZONE 'Europe/Moscow',
    ALTER COLUMN insulin_dose_created_at TYPE TIMESTAMPTZ USING insulin_dose_created_at AT TIME ZONE 'Europe/Moscow',
    ALTER COLUMN insulin_dose_updated_at TYPE TIMESTAMPTZ USING insulin_dose_updated_at AT TIME ZONE 'Europe/Moscow';
