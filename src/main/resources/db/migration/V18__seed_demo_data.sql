-- V28: Seed demo data — patients, doctors, admin
-- Пароль всех seed-пользователей: password
-- BCrypt(10) hash: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
--
-- ВАЖНО: наличие admin в этой миграции отключает создание дефолтного admin@telemed.ru
-- через DataInitializer (он срабатывает только при count = 0).
-- Вход в панель администратора: admin@telemed.ru / password

-- ──────────────────────────────────────────────
-- Регион и город
-- ──────────────────────────────────────────────
INSERT INTO
    regions (region_name)
VALUES ('Москва и Московская область') ON CONFLICT (region_name) DO NOTHING;

INSERT INTO
    cities (city_name, region_id)
VALUES (
        'Москва',
        (
            SELECT region_id
            FROM regions
            WHERE
                region_name = 'Москва и Московская область'
        )
    ) ON CONFLICT (city_name, region_id) DO NOTHING;

-- ──────────────────────────────────────────────
-- Медицинская организация
-- ──────────────────────────────────────────────
INSERT INTO
    medical_organizations (
        organization_name,
        organization_address,
        organization_email,
        organization_phone,
        organization_city
    )
VALUES (
        'Городская клиника №1',
        'г. Москва, ул. Академика Сахарова, д. 12',
        'clinic1@telemed.ru',
        '+7 (495) 123-45-67',
        (
            SELECT city_id
            FROM cities
            WHERE
                city_name = 'Москва'
        )
    );

-- ──────────────────────────────────────────────
-- Администратор
-- ──────────────────────────────────────────────
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Главный',
                'Администратор',
                NULL,
                'admin@telemed.ru',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    admins (admin_id)
SELECT user_id
FROM u;

-- ──────────────────────────────────────────────
-- Пациенты
-- ──────────────────────────────────────────────

-- Пациент 1: Иван Петров — Диабет 2 типа
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_date_of_birth,
                user_gender,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Иван',
                'Петров',
                'Сергеевич',
                '1985-03-15',
                'M',
                'ivan.petrov@example.com',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    patients (
        patient_id,
        patient_diabetes_type,
        patient_target_low,
        patient_target_high,
        patient_insulin_dependency,
        patient_blood_type,
        patient_height_cm
    )
SELECT user_id, 'TYPE_2', 4.0, 9.0, false, 'B+', 180
FROM u;

-- Пациент 2: Анна Смирнова — Диабет 1 типа, инсулинозависима
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_date_of_birth,
                user_gender,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Анна',
                'Смирнова',
                'Владимировна',
                '1992-07-22',
                'F',
                'anna.smirnova@example.com',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    patients (
        patient_id,
        patient_diabetes_type,
        patient_target_low,
        patient_target_high,
        patient_insulin_dependency,
        patient_blood_type,
        patient_height_cm,
        patient_hba1c
    )
SELECT user_id, 'TYPE_1', 3.9, 8.0, true, 'A+', 165, 7.2
FROM u;

-- Пациент 3: Михаил Волков — без уточнённого диагноза
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_date_of_birth,
                user_gender,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Михаил',
                'Волков',
                'Андреевич',
                '1978-11-05',
                'M',
                'mikhail.volkov@example.com',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    patients (
        patient_id,
        patient_target_low,
        patient_target_high,
        patient_insulin_dependency
    )
SELECT user_id, 3.9, 10.0, false
FROM u;

-- ──────────────────────────────────────────────
-- Врачи
-- ──────────────────────────────────────────────

-- Врач 1: Кузнецов Алексей Иванович — Эндокринолог
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_date_of_birth,
                user_gender,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Алексей',
                'Кузнецов',
                'Иванович',
                '1975-06-10',
                'M',
                'a.kuznetsov@telemed.ru',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    doctors (
        doctor_id,
        doctor_specialization_id,
        doctor_experience,
        doctor_qualification,
        doctor_organization_id,
        doctor_about
    )
SELECT u.user_id, (
        SELECT specialization_id
        FROM specializations
        WHERE
            specialization_name = 'Эндокринолог'
    ), 15, 'Высшая категория', (
        SELECT organization_id
        FROM medical_organizations
        WHERE
            organization_email = 'clinic1@telemed.ru'
    ), 'Специалист по диагностике и лечению сахарного диабета 1 и 2 типа. Опыт 15 лет.'
FROM u;

-- Врач 2: Морозова Елена Сергеевна — Диабетолог
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_date_of_birth,
                user_gender,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Елена',
                'Морозова',
                'Сергеевна',
                '1980-02-14',
                'F',
                'e.morozova@telemed.ru',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    doctors (
        doctor_id,
        doctor_specialization_id,
        doctor_experience,
        doctor_qualification,
        doctor_organization_id,
        doctor_about
    )
SELECT u.user_id, (
        SELECT specialization_id
        FROM specializations
        WHERE
            specialization_name = 'Диабетолог'
    ), 12, 'Первая категория', (
        SELECT organization_id
        FROM medical_organizations
        WHERE
            organization_email = 'clinic1@telemed.ru'
    ), 'Лечение диабета всех типов, подбор инсулинотерапии, контроль гликемии. Опыт 12 лет.'
FROM u;

-- Врач 3: Соколов Дмитрий Александрович — Диетолог
WITH
    u AS (
        INSERT INTO
            users (
                user_first_name,
                user_last_name,
                user_middle_name,
                user_date_of_birth,
                user_gender,
                user_email,
                user_password_hash,
                user_status
            )
        VALUES (
                'Дмитрий',
                'Соколов',
                'Александрович',
                '1988-09-30',
                'M',
                'd.sokolov@telemed.ru',
                '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
                'ACTIVE'
            ) RETURNING user_id
    )
INSERT INTO
    doctors (
        doctor_id,
        doctor_specialization_id,
        doctor_experience,
        doctor_qualification,
        doctor_about
    )
SELECT u.user_id, (
        SELECT specialization_id
        FROM specializations
        WHERE
            specialization_name = 'Диетолог'
    ), 8, 'Вторая категория', 'Составляет индивидуальные планы питания для пациентов с сахарным диабетом. Опыт 8 лет.'
FROM u;