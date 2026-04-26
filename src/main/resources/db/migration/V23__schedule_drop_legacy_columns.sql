-- Слоты теперь хранятся как явная коллекция schedule_slots (start_time, end_time, consultation_type).
-- Расписание дня = dayOfWeek + список слотов; авто-генерация — одноразовый помощник, заполняющий список.

ALTER TABLE doctor_schedules DROP COLUMN IF EXISTS schedule_start_time;
ALTER TABLE doctor_schedules DROP COLUMN IF EXISTS schedule_end_time;
ALTER TABLE doctor_schedules DROP COLUMN IF EXISTS schedule_slot_duration_minutes;
