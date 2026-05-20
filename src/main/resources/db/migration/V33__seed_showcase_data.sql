-- V33: Витринный сид для демонстрации возможностей системы.
-- Источники данных:
--   • врач Кузнецов А.И. (a.kuznetsov@telemed.ru) — создан в V18, эндокринолог.
--     В этой миграции получает расписание Mon–Fri, слоты и пациентов.
--   • пациент Сергей Орлов (sergey.orlov@example.com) — НОВЫЙ, СД-2 на
--     комбинированной терапии (метформин + базальный инсулин), с полной
--     историей самоконтроля, питания, приёмов и медкарты.
--   • существующие из V18 пациенты Анна Смирнова и Иван Петров — также
--     прикрепляются к Кузнецову, у каждого по приёму, чтобы у врача
--     был "живой" список пациентов.
--
-- Пароль ВСЕХ seed-пользователей: password
-- BCrypt(10) hash: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
--
-- Миграция оформлена единым DO $$ блоком, чтобы можно было хранить ID
-- врача / пациента / расписания / приёмов / приёмов пищи в локальных
-- переменных и не дублировать SELECT'ы по email.

DO $$
DECLARE
    -- ──────────── Существующие записи (из V18) ────────────
    v_doctor_id        BIGINT;
    v_patient_smirnova BIGINT;
    v_patient_petrov   BIGINT;
    v_organization_id  BIGINT;

    -- ──────────── Новый витринный пациент ────────────
    v_patient_orlov    BIGINT;

    -- ──────────── Расписание ────────────
    v_sch_mon BIGINT; v_sch_tue BIGINT; v_sch_wed BIGINT;
    v_sch_thu BIGINT; v_sch_fri BIGINT;

    -- ──────────── Приёмы (для удобства ссылаться из medical_record/review) ────────────
    v_app_completed_1 BIGINT;  -- Орлов, завершён 30 дн. назад
    v_app_completed_2 BIGINT;  -- Орлов, завершён 14 дн. назад
    v_app_canceled    BIGINT;  -- Орлов, отменён
    v_app_no_show     BIGINT;  -- Орлов, не явился
    v_app_created_1   BIGINT;  -- Орлов, ждёт врача (пациент уже подтвердил)
    v_app_created_2   BIGINT;  -- Орлов, ждёт пациента (врач уже подтвердил)
    v_app_confirmed   BIGINT;  -- Орлов, обе стороны подтвердили
    v_app_smirnova_1  BIGINT;  -- Анна, завершён + отзыв
    v_app_smirnova_2  BIGINT;  -- Анна, будущий CREATED
    v_app_petrov_1    BIGINT;  -- Иван, CONFIRMED

    -- ──────────── Запись в медкарте ────────────
    v_record_general BIGINT;  -- "первичный приём" без appointment_id
    v_record_app_1   BIGINT;  -- привязана к v_app_completed_1
    v_record_app_2   BIGINT;  -- привязана к v_app_completed_2
    v_record_smirnova BIGINT; -- запись для Анны

    -- ──────────── Питание ────────────
    v_dish_oatmeal     BIGINT;
    v_dish_buckwheat   BIGINT;
    v_dish_chicken     BIGINT;
    v_dish_apple       BIGINT;
    v_dish_cucumber    BIGINT;
    v_dish_egg         BIGINT;
    v_dish_kefir       BIGINT;
    v_dish_bread_rye   BIGINT;
    v_dish_cottage     BIGINT;
    v_dish_salmon      BIGINT;
    v_dish_brokkoli    BIGINT;
    v_dish_almonds     BIGINT;
    v_dish_olive       BIGINT;
    v_dish_tomato      BIGINT;
    v_dish_avocado     BIGINT;
    -- Личные блюда Орлова
    v_dish_homemade_soup BIGINT;
    v_dish_protein_bar   BIGINT;

    -- ──────────── Дни питания и приёмы пищи ────────────
    v_day_today      BIGINT;
    v_day_yesterday  BIGINT;
    v_day_minus2     BIGINT;
    v_meal_breakfast BIGINT;
    v_meal_lunch     BIGINT;
    v_meal_dinner    BIGINT;
    v_meal_snack     BIGINT;
    v_meal_y_breakfast BIGINT;
    v_meal_y_lunch     BIGINT;

    -- ──────────── Чат ────────────
    v_chat_id BIGINT;

    -- ──────────── Reminder ────────────
    v_reminder_recur BIGINT;
    v_reminder_once  BIGINT;
BEGIN
    -- ============================================================
    -- 1. Получаем уже существующие ID врача, пациентов, организации
    -- ============================================================
    SELECT user_id INTO v_doctor_id FROM users
        WHERE user_email = 'a.kuznetsov@telemed.ru';
    SELECT user_id INTO v_patient_smirnova FROM users
        WHERE user_email = 'anna.smirnova@example.com';
    SELECT user_id INTO v_patient_petrov FROM users
        WHERE user_email = 'ivan.petrov@example.com';
    SELECT organization_id INTO v_organization_id FROM medical_organizations
        WHERE organization_email = 'clinic1@telemed.ru';

    IF v_doctor_id IS NULL OR v_patient_smirnova IS NULL OR v_patient_petrov IS NULL THEN
        RAISE EXCEPTION 'V33: ожидаемые seed-пользователи из V18 отсутствуют — V18 должна быть применена раньше.';
    END IF;

    -- ============================================================
    -- 2. Витринный пациент: Сергей Михайлович Орлов (СД-2 + базальный инсулин)
    -- ============================================================
    INSERT INTO users (user_first_name, user_last_name, user_middle_name,
                       user_date_of_birth, user_gender,
                       user_email, user_password_hash, user_status, user_time_zone)
    VALUES ('Сергей', 'Орлов', 'Михайлович',
            '1972-04-18', 'M',
            'sergey.orlov@example.com',
            '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
            'ACTIVE', 'Europe/Moscow')
    RETURNING user_id INTO v_patient_orlov;

    INSERT INTO patients (patient_id,
                          patient_diabetes_type, patient_diagnosis_date,
                          patient_target_low, patient_target_high,
                          patient_insulin_dependency,
                          patient_blood_type, patient_height_cm, patient_hba1c,
                          patient_daily_calories_goal, patient_daily_protein_goal,
                          patient_daily_fats_goal, patient_daily_carbs_goal)
    VALUES (v_patient_orlov,
            'TYPE_2', DATE '2018-09-12',
            4.0, 9.0, TRUE,
            'O+', 178, 7.4,
            2200, 110.0, 70.0, 250.0);

    -- ============================================================
    -- 3. Догружаем целевые показатели и hba1c уже существующим пациентам
    --    (V18 их не задавала — без этого графики и прогресс смотрятся пустыми)
    -- ============================================================
    UPDATE patients
       SET patient_diagnosis_date     = DATE '2010-05-20',
           patient_hba1c              = 7.6,
           patient_daily_calories_goal = 1800,
           patient_daily_protein_goal  = 90.0,
           patient_daily_fats_goal     = 60.0,
           patient_daily_carbs_goal    = 200.0
     WHERE patient_id = v_patient_smirnova;

    UPDATE patients
       SET patient_diagnosis_date     = DATE '2020-01-15',
           patient_hba1c              = 8.1,
           patient_daily_calories_goal = 2000,
           patient_daily_protein_goal  = 100.0,
           patient_daily_fats_goal     = 65.0,
           patient_daily_carbs_goal    = 230.0
     WHERE patient_id = v_patient_petrov;

    -- ============================================================
    -- 4. Привязки пациент ↔ врач (Кузнецов ведёт всех троих)
    -- ============================================================
    INSERT INTO patient_doctor_assignments (patient_id, doctor_id, is_active)
    VALUES
        (v_patient_orlov,    v_doctor_id, TRUE),
        (v_patient_smirnova, v_doctor_id, TRUE),
        (v_patient_petrov,   v_doctor_id, TRUE)
    ON CONFLICT (patient_id, doctor_id) DO NOTHING;

    -- ============================================================
    -- 5. Расписание Кузнецова: Пн–Пт, по несколько слотов на день
    -- ============================================================
    INSERT INTO doctor_schedules (doctor_id, schedule_day_of_week)
    VALUES (v_doctor_id, 'MONDAY')    RETURNING schedule_id INTO v_sch_mon;
    INSERT INTO doctor_schedules (doctor_id, schedule_day_of_week)
    VALUES (v_doctor_id, 'TUESDAY')   RETURNING schedule_id INTO v_sch_tue;
    INSERT INTO doctor_schedules (doctor_id, schedule_day_of_week)
    VALUES (v_doctor_id, 'WEDNESDAY') RETURNING schedule_id INTO v_sch_wed;
    INSERT INTO doctor_schedules (doctor_id, schedule_day_of_week)
    VALUES (v_doctor_id, 'THURSDAY')  RETURNING schedule_id INTO v_sch_thu;
    INSERT INTO doctor_schedules (doctor_id, schedule_day_of_week)
    VALUES (v_doctor_id, 'FRIDAY')    RETURNING schedule_id INTO v_sch_fri;

    -- На каждый день: 09:00, 10:00, 11:00, 14:00, 15:00 — VIDEO,
    -- + один AUDIO-слот в обед (13:30) для тех, у кого плохой канал.
    INSERT INTO schedule_slots (schedule_id, start_time, end_time, consultation_type) VALUES
        (v_sch_mon, '09:00', '09:30', 'VIDEO'),
        (v_sch_mon, '10:00', '10:30', 'VIDEO'),
        (v_sch_mon, '11:00', '11:30', 'VIDEO'),
        (v_sch_mon, '13:30', '14:00', 'AUDIO'),
        (v_sch_mon, '14:30', '15:00', 'VIDEO'),
        (v_sch_mon, '15:00', '15:30', 'VIDEO'),

        (v_sch_tue, '09:00', '09:30', 'VIDEO'),
        (v_sch_tue, '10:00', '10:30', 'VIDEO'),
        (v_sch_tue, '11:00', '11:30', 'VIDEO'),
        (v_sch_tue, '14:00', '14:30', 'VIDEO'),
        (v_sch_tue, '15:00', '15:30', 'VIDEO'),

        (v_sch_wed, '09:00', '09:30', 'VIDEO'),
        (v_sch_wed, '10:00', '10:30', 'VIDEO'),
        (v_sch_wed, '11:00', '11:30', 'VIDEO'),
        (v_sch_wed, '13:30', '14:00', 'AUDIO'),
        (v_sch_wed, '15:00', '15:30', 'VIDEO'),

        (v_sch_thu, '09:00', '09:30', 'VIDEO'),
        (v_sch_thu, '10:00', '10:30', 'VIDEO'),
        (v_sch_thu, '11:00', '11:30', 'VIDEO'),
        (v_sch_thu, '14:00', '14:30', 'VIDEO'),
        (v_sch_thu, '15:00', '15:30', 'VIDEO'),

        (v_sch_fri, '09:00', '09:30', 'VIDEO'),
        (v_sch_fri, '10:00', '10:30', 'VIDEO'),
        (v_sch_fri, '11:00', '11:30', 'VIDEO'),
        (v_sch_fri, '13:30', '14:00', 'AUDIO'),
        (v_sch_fri, '14:30', '15:00', 'VIDEO');

    -- ============================================================
    -- 6. FINDRISC — два прохождения теста Орлова: за полгода и сейчас
    -- ============================================================
    INSERT INTO test_results (patient_id, test_points_age, test_points_bmi, test_points_waist,
                              test_points_vegetable, test_points_activity, test_points_hypertension,
                              test_points_glucose, test_points_history, test_points_total,
                              test_created_at)
    VALUES
        (v_patient_orlov, 3, 3, 4, 1, 2, 2, 5, 3, 23, (NOW() - INTERVAL '180 days')::TIMESTAMP),
        (v_patient_orlov, 3, 1, 3, 0, 0, 2, 5, 3, 17, (NOW() - INTERVAL '7 days')::TIMESTAMP);

    -- ============================================================
    -- 7. Аллергии / лекарства / события медкарты для Орлова
    -- ============================================================
    INSERT INTO allergies (patient_id, allergen, reaction) VALUES
        (v_patient_orlov, 'Пенициллин',   'Крапивница, отёк Квинке в анамнезе'),
        (v_patient_orlov, 'Орехи (фундук)', 'Зуд, лёгкий отёк слизистой'),
        (v_patient_smirnova, 'Йод',        'Кожная сыпь');

    INSERT INTO medications (patient_id, name, dosage, frequency, start_date, end_date, active) VALUES
        (v_patient_orlov, 'Метформин',                   '1000 мг',    '2 раза в день, утром и вечером',
            DATE '2018-09-20', NULL, TRUE),
        (v_patient_orlov, 'Гларгин (инсулин длительный)', '18 ЕД',      '1 раз в день, перед сном',
            DATE '2022-03-01', NULL, TRUE),
        (v_patient_orlov, 'Аторвастатин',                '20 мг',      '1 раз в день, вечером',
            DATE '2021-06-10', NULL, TRUE),
        (v_patient_orlov, 'Лизиноприл',                  '10 мг',      '1 раз в день, утром',
            DATE '2019-11-01', DATE '2023-04-15', FALSE),
        (v_patient_smirnova, 'Аспарт (инсулин ультракороткий)', '6–10 ЕД', 'Перед каждым приёмом пищи',
            DATE '2015-02-10', NULL, TRUE),
        (v_patient_smirnova, 'Деглудек (инсулин длительный)',   '24 ЕД',  '1 раз в сутки',
            DATE '2018-04-05', NULL, TRUE),
        (v_patient_petrov, 'Метформин',                  '850 мг',     '2 раза в день',
            DATE '2020-02-01', NULL, TRUE);

    INSERT INTO medical_events (patient_id, description, event_date, end_date, event_type) VALUES
        (v_patient_orlov,
         'Дебют сахарного диабета 2 типа. Глюкоза натощак 11.4 ммоль/л, HbA1c 8.9%. Назначен метформин.',
         DATE '2018-09-12', NULL, 'Установка диагноза'),
        (v_patient_orlov,
         'Госпитализация в эндокринологическое отделение для подбора инсулинотерапии (комбинация с метформином).',
         DATE '2022-02-25', DATE '2022-03-04', 'Госпитализация'),
        (v_patient_orlov,
         'Лазерная коагуляция сетчатки правого глаза по поводу непролиферативной диабетической ретинопатии.',
         DATE '2023-08-10', NULL, 'Хирургическое вмешательство'),
        (v_patient_smirnova,
         'Дебют СД 1 типа в 22 года. Стационарное лечение, перевод на базис-болюсную инсулинотерапию.',
         DATE '2014-09-03', DATE '2014-09-14', 'Установка диагноза');

    -- ============================================================
    -- 8. Самоконтроль: гликемия Орлова (последние 14 дней, 4–6 измерений в день)
    -- ============================================================
    -- Чтобы не плодить 80 строк, генерируем сериями через generate_series
    -- по сценарным точкам дня. Уровни — реалистичный разброс СД-2 на терапии.
    INSERT INTO glycemia (patient_id, glycemia_level, glycemia_type, glycemia_created_at, glycemia_updated_at)
    SELECT v_patient_orlov,
           level,
           gtype,
           ts,
           ts
    FROM (
        SELECT
            (NOW() - (d || ' days')::INTERVAL)::TIMESTAMP + (h || ' hours')::INTERVAL AS ts,
            CASE h
                WHEN 7  THEN 'FASTING'
                WHEN 9  THEN 'AFTER_BREAKFAST'
                WHEN 13 THEN 'BEFORE_LUNCH'
                WHEN 15 THEN 'AFTER_LUNCH'
                WHEN 19 THEN 'BEFORE_DINNER'
                WHEN 22 THEN 'BEFORE_BEDTIME'
            END AS gtype,
            CASE h
                WHEN 7  THEN 5.6 + (random() * 1.4)
                WHEN 9  THEN 7.2 + (random() * 2.1)
                WHEN 13 THEN 5.0 + (random() * 1.8)
                WHEN 15 THEN 7.0 + (random() * 2.4)
                WHEN 19 THEN 5.5 + (random() * 1.6)
                WHEN 22 THEN 6.4 + (random() * 1.8)
            END AS level
        FROM generate_series(0, 13) d
        CROSS JOIN (VALUES (7), (9), (13), (15), (19), (22)) AS hours(h)
    ) data;

    -- Гликемия Анны (СД-1, нагляднее: ниже базальный, чаще измеряет, выше пики)
    INSERT INTO glycemia (patient_id, glycemia_level, glycemia_type, glycemia_created_at, glycemia_updated_at)
    SELECT v_patient_smirnova,
           CASE h
               WHEN 7  THEN 4.5 + (random() * 1.8)
               WHEN 10 THEN 8.0 + (random() * 3.0)
               WHEN 14 THEN 5.5 + (random() * 2.5)
               WHEN 16 THEN 7.5 + (random() * 3.0)
               WHEN 21 THEN 5.8 + (random() * 2.5)
           END,
           CASE h
               WHEN 7  THEN 'FASTING'
               WHEN 10 THEN 'AFTER_BREAKFAST'
               WHEN 14 THEN 'BEFORE_LUNCH'
               WHEN 16 THEN 'AFTER_LUNCH'
               WHEN 21 THEN 'BEFORE_BEDTIME'
           END,
           ts, ts
    FROM (
        SELECT
            (NOW() - (d || ' days')::INTERVAL)::TIMESTAMP + (h || ' hours')::INTERVAL AS ts,
            h
        FROM generate_series(0, 9) d
        CROSS JOIN (VALUES (7), (10), (14), (16), (21)) AS hours(h)
    ) data;

    -- ============================================================
    -- 9. Самоконтроль: давление, вес, симптомы, активность
    -- ============================================================
    -- Давление — раз в 2 дня
    INSERT INTO blood_pressures (patient_id, blood_pressure_systolic, blood_pressure_diastolic,
                                 blood_pressure_created_at, blood_pressure_updated_at)
    SELECT v_patient_orlov,
           128 + (random() * 14)::INT,
           80  + (random() * 8)::INT,
           ts, ts
    FROM (
        SELECT (NOW() - (d || ' days')::INTERVAL)::TIMESTAMP AS ts
        FROM generate_series(0, 28, 2) d
    ) data;

    -- Вес — раз в неделю, лёгкая динамика
    INSERT INTO weight (patient_id, weight_value, weight_created_at, weight_updated_at) VALUES
        (v_patient_orlov, 92.4, (NOW() - INTERVAL '56 days')::TIMESTAMP, (NOW() - INTERVAL '56 days')::TIMESTAMP),
        (v_patient_orlov, 91.9, (NOW() - INTERVAL '49 days')::TIMESTAMP, (NOW() - INTERVAL '49 days')::TIMESTAMP),
        (v_patient_orlov, 91.4, (NOW() - INTERVAL '42 days')::TIMESTAMP, (NOW() - INTERVAL '42 days')::TIMESTAMP),
        (v_patient_orlov, 90.8, (NOW() - INTERVAL '35 days')::TIMESTAMP, (NOW() - INTERVAL '35 days')::TIMESTAMP),
        (v_patient_orlov, 90.6, (NOW() - INTERVAL '28 days')::TIMESTAMP, (NOW() - INTERVAL '28 days')::TIMESTAMP),
        (v_patient_orlov, 90.1, (NOW() - INTERVAL '21 days')::TIMESTAMP, (NOW() - INTERVAL '21 days')::TIMESTAMP),
        (v_patient_orlov, 89.7, (NOW() - INTERVAL '14 days')::TIMESTAMP, (NOW() - INTERVAL '14 days')::TIMESTAMP),
        (v_patient_orlov, 89.3, (NOW() - INTERVAL '7 days')::TIMESTAMP,  (NOW() - INTERVAL '7 days')::TIMESTAMP),
        (v_patient_orlov, 88.9,  NOW()::TIMESTAMP,                       NOW()::TIMESTAMP),
        (v_patient_smirnova, 58.4, (NOW() - INTERVAL '14 days')::TIMESTAMP, (NOW() - INTERVAL '14 days')::TIMESTAMP),
        (v_patient_smirnova, 58.2, (NOW() - INTERVAL '7 days')::TIMESTAMP,  (NOW() - INTERVAL '7 days')::TIMESTAMP);

    -- Симптомы
    INSERT INTO symptoms (patient_id, symptom_severity, symptom_description,
                          symptom_created_at, symptom_updated_at) VALUES
        (v_patient_orlov, 'MILD',     'Лёгкое покалывание в стопах к вечеру',
            (NOW() - INTERVAL '20 days')::TIMESTAMP, (NOW() - INTERVAL '20 days')::TIMESTAMP),
        (v_patient_orlov, 'MODERATE', 'Сухость во рту, повышенная жажда после соленого ужина',
            (NOW() - INTERVAL '14 days')::TIMESTAMP, (NOW() - INTERVAL '14 days')::TIMESTAMP),
        (v_patient_orlov, 'MILD',     'Усталость к концу рабочего дня',
            (NOW() - INTERVAL '10 days')::TIMESTAMP, (NOW() - INTERVAL '10 days')::TIMESTAMP),
        (v_patient_orlov, 'NONE',     'Самочувствие хорошее, без жалоб',
            (NOW() - INTERVAL '3 days')::TIMESTAMP, (NOW() - INTERVAL '3 days')::TIMESTAMP),
        (v_patient_smirnova, 'SEVERE', 'Эпизод гипогликемии 2.8 ммоль/л после спорта, купирована соком',
            (NOW() - INTERVAL '6 days')::TIMESTAMP, (NOW() - INTERVAL '6 days')::TIMESTAMP);

    -- Физическая активность
    INSERT INTO physical_activities (patient_id, physical_activity_type, physical_activity_duration,
                                     physical_activity_intensity,
                                     physical_activity_created_at, physical_activity_updated_at) VALUES
        (v_patient_orlov, 'WALKING',          45, 'LOW',
            (NOW() - INTERVAL '12 days')::TIMESTAMP, (NOW() - INTERVAL '12 days')::TIMESTAMP),
        (v_patient_orlov, 'WALKING',          50, 'LOW',
            (NOW() - INTERVAL '10 days')::TIMESTAMP, (NOW() - INTERVAL '10 days')::TIMESTAMP),
        (v_patient_orlov, 'CYCLING',          30, 'MEDIUM',
            (NOW() - INTERVAL '8 days')::TIMESTAMP,  (NOW() - INTERVAL '8 days')::TIMESTAMP),
        (v_patient_orlov, 'WALKING',          60, 'LOW',
            (NOW() - INTERVAL '5 days')::TIMESTAMP,  (NOW() - INTERVAL '5 days')::TIMESTAMP),
        (v_patient_orlov, 'STRENGTH_TRAINING', 40, 'MEDIUM',
            (NOW() - INTERVAL '3 days')::TIMESTAMP,  (NOW() - INTERVAL '3 days')::TIMESTAMP),
        (v_patient_orlov, 'WALKING',          55, 'LOW',
            (NOW() - INTERVAL '1 days')::TIMESTAMP,  (NOW() - INTERVAL '1 days')::TIMESTAMP),
        (v_patient_smirnova, 'YOGA',          40, 'LOW',
            (NOW() - INTERVAL '4 days')::TIMESTAMP,  (NOW() - INTERVAL '4 days')::TIMESTAMP);

    -- ============================================================
    -- 10. Каталог блюд: ПУБЛИЧНЫЕ (patient_id = NULL) — общая база
    -- ============================================================
    -- Калории / Углеводы / Белки / Жиры на 100 г.
    INSERT INTO dish (patient_id, dish_name, dish_calories_per_100,
                      dish_carbs_per_100, dish_protein_per_100, dish_fats_per_100, dish_description) VALUES
        (NULL, 'Овсяная каша на воде',     88,  15.0, 3.0, 1.7, 'Без сахара, на воде. Низкий ГИ.'),
        (NULL, 'Гречневая каша варёная',   132, 25.0, 4.5, 1.4, 'Источник медленных углеводов.'),
        (NULL, 'Куриная грудка отварная',  165, 0.0, 31.0, 3.6, 'Постный белок без жира.'),
        (NULL, 'Яблоко (зелёное)',         52,  14.0, 0.3, 0.2, 'Свежее, среднего размера.'),
        (NULL, 'Огурец свежий',            16,  3.6, 0.7, 0.1, 'Низкокалорийный овощ.'),
        (NULL, 'Яйцо куриное варёное',     155, 1.1, 13.0, 11.0, 'Стандартное варёное яйцо.'),
        (NULL, 'Кефир 1%',                 40,  4.0, 3.0, 1.0, 'Кисломолочный продукт.'),
        (NULL, 'Хлеб ржаной',              259, 47.0, 9.0, 3.3, '1 кусок ≈ 30 г = 1 ХЕ.'),
        (NULL, 'Творог 5%',                121, 1.8, 17.2, 5.0, 'Источник медленного белка.'),
        (NULL, 'Лосось запечённый',        208, 0.0, 22.0, 13.0, 'Богат омега-3.'),
        (NULL, 'Брокколи на пару',         34,  6.6, 2.8, 0.4, 'Зелёный овощ, низкокалорийный.'),
        (NULL, 'Миндаль сырой',            579, 21.5, 21.0, 49.0, 'Полезный жир, аккуратно с порцией.'),
        (NULL, 'Оливковое масло',          884, 0.0, 0.0, 100.0, 'Заправка для салатов.'),
        (NULL, 'Помидор свежий',           18,  3.9, 0.9, 0.2, 'Сезонный овощ.'),
        (NULL, 'Авокадо',                  160, 8.5, 2.0, 14.7, 'Источник полезных жиров.')
    RETURNING dish_id INTO v_dish_oatmeal;
    -- RETURNING из multi-VALUES возвращает только последнюю строку,
    -- поэтому подгружаем нужные ID отдельно через имя:
    SELECT dish_id INTO v_dish_oatmeal   FROM dish WHERE dish_name = 'Овсяная каша на воде'    AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_buckwheat FROM dish WHERE dish_name = 'Гречневая каша варёная'  AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_chicken   FROM dish WHERE dish_name = 'Куриная грудка отварная' AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_apple     FROM dish WHERE dish_name = 'Яблоко (зелёное)'        AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_cucumber  FROM dish WHERE dish_name = 'Огурец свежий'           AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_egg       FROM dish WHERE dish_name = 'Яйцо куриное варёное'    AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_kefir     FROM dish WHERE dish_name = 'Кефир 1%'                AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_bread_rye FROM dish WHERE dish_name = 'Хлеб ржаной'             AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_cottage   FROM dish WHERE dish_name = 'Творог 5%'               AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_salmon    FROM dish WHERE dish_name = 'Лосось запечённый'       AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_brokkoli  FROM dish WHERE dish_name = 'Брокколи на пару'        AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_almonds   FROM dish WHERE dish_name = 'Миндаль сырой'           AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_olive     FROM dish WHERE dish_name = 'Оливковое масло'         AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_tomato    FROM dish WHERE dish_name = 'Помидор свежий'          AND patient_id IS NULL;
    SELECT dish_id INTO v_dish_avocado   FROM dish WHERE dish_name = 'Авокадо'                 AND patient_id IS NULL;

    -- ============================================================
    -- 11. ЛИЧНЫЕ блюда Орлова (patient_id = его id)
    -- ============================================================
    INSERT INTO dish (patient_id, dish_name, dish_calories_per_100,
                      dish_carbs_per_100, dish_protein_per_100, dish_fats_per_100, dish_description)
    VALUES
        (v_patient_orlov, 'Домашний овощной суп с куриной грудкой',
            42, 4.2, 5.5, 0.9, 'Морковь, лук, сельдерей, грудка. Без картофеля.')
    RETURNING dish_id INTO v_dish_homemade_soup;

    INSERT INTO dish (patient_id, dish_name, dish_calories_per_100,
                      dish_carbs_per_100, dish_protein_per_100, dish_fats_per_100, dish_description)
    VALUES
        (v_patient_orlov, 'Протеиновый батончик «лёгкий»',
            340, 25.0, 30.0, 12.0, 'Без сахара, с эритритом. На перекус.')
    RETURNING dish_id INTO v_dish_protein_bar;

    -- ============================================================
    -- 12. Дневник питания: 3 дня (сегодня, вчера, позавчера)
    -- ============================================================
    INSERT INTO nutrition_day (patient_id, diary_date, completed)
    VALUES (v_patient_orlov, CURRENT_DATE,             FALSE)
    RETURNING nutrition_day_id INTO v_day_today;

    INSERT INTO nutrition_day (patient_id, diary_date, completed)
    VALUES (v_patient_orlov, CURRENT_DATE - 1,         TRUE)
    RETURNING nutrition_day_id INTO v_day_yesterday;

    INSERT INTO nutrition_day (patient_id, diary_date, completed)
    VALUES (v_patient_orlov, CURRENT_DATE - 2,         TRUE)
    RETURNING nutrition_day_id INTO v_day_minus2;

    -- Сегодня: завтрак + обед + перекус
    INSERT INTO meal (patient_id, nutrition_day_id, meal_datetime, meal_type)
    VALUES (v_patient_orlov, v_day_today, (CURRENT_DATE + TIME '08:30'), 'BREAKFAST')
    RETURNING meal_id INTO v_meal_breakfast;

    INSERT INTO meal (patient_id, nutrition_day_id, meal_datetime, meal_type)
    VALUES (v_patient_orlov, v_day_today, (CURRENT_DATE + TIME '13:15'), 'LUNCH')
    RETURNING meal_id INTO v_meal_lunch;

    INSERT INTO meal (patient_id, nutrition_day_id, meal_datetime, meal_type)
    VALUES (v_patient_orlov, v_day_today, (CURRENT_DATE + TIME '17:00'), 'SNACK')
    RETURNING meal_id INTO v_meal_snack;

    -- Сегодня вечером ещё может быть ужин — оставим пустым,
    -- так нагляднее показывается прогресс-бар "не хватает калорий".

    -- Вчера: завтрак + обед + ужин
    INSERT INTO meal (patient_id, nutrition_day_id, meal_datetime, meal_type)
    VALUES (v_patient_orlov, v_day_yesterday, (CURRENT_DATE - 1 + TIME '08:00'), 'BREAKFAST')
    RETURNING meal_id INTO v_meal_y_breakfast;

    INSERT INTO meal (patient_id, nutrition_day_id, meal_datetime, meal_type)
    VALUES (v_patient_orlov, v_day_yesterday, (CURRENT_DATE - 1 + TIME '13:30'), 'LUNCH')
    RETURNING meal_id INTO v_meal_y_lunch;

    INSERT INTO meal (patient_id, nutrition_day_id, meal_datetime, meal_type)
    VALUES (v_patient_orlov, v_day_yesterday, (CURRENT_DATE - 1 + TIME '19:00'), 'DINNER')
    RETURNING meal_id INTO v_meal_dinner;

    -- ─── Позиции: snapshot KBJU копируем из dish (как делает MealService.addItem) ─────
    -- helper: bread_units = carbs / 12

    -- Завтрак (сегодня): овсянка 200 г + яблоко 150 г + кефир 200 г
    INSERT INTO meal_item (meal_id, dish_id, meal_item_portion_grams,
                           meal_item_calories_per_100, meal_item_carbs_per_100,
                           meal_item_protein_per_100, meal_item_fats_per_100,
                           meal_item_carbs_per_100_bread_unit, meal_item_quantity)
    SELECT v_meal_breakfast, dish_id, portion,
           dish_calories_per_100, dish_carbs_per_100,
           dish_protein_per_100, dish_fats_per_100,
           dish_carbs_per_100 / 12.0, 1
    FROM dish, (VALUES (v_dish_oatmeal, 200), (v_dish_apple, 150), (v_dish_kefir, 200)) AS t(d_id, portion)
    WHERE dish.dish_id = t.d_id;

    -- Обед (сегодня): гречка 200 г + куриная грудка 150 г + овощи (огурец 100 г + помидор 100 г) + хлеб 30 г
    INSERT INTO meal_item (meal_id, dish_id, meal_item_portion_grams,
                           meal_item_calories_per_100, meal_item_carbs_per_100,
                           meal_item_protein_per_100, meal_item_fats_per_100,
                           meal_item_carbs_per_100_bread_unit, meal_item_quantity)
    SELECT v_meal_lunch, dish_id, portion,
           dish_calories_per_100, dish_carbs_per_100,
           dish_protein_per_100, dish_fats_per_100,
           dish_carbs_per_100 / 12.0, 1
    FROM dish, (VALUES
        (v_dish_buckwheat, 200),
        (v_dish_chicken,   150),
        (v_dish_cucumber,  100),
        (v_dish_tomato,    100),
        (v_dish_bread_rye,  30)
    ) AS t(d_id, portion)
    WHERE dish.dish_id = t.d_id;

    -- Перекус (сегодня): творог 150 г + миндаль 30 г
    INSERT INTO meal_item (meal_id, dish_id, meal_item_portion_grams,
                           meal_item_calories_per_100, meal_item_carbs_per_100,
                           meal_item_protein_per_100, meal_item_fats_per_100,
                           meal_item_carbs_per_100_bread_unit, meal_item_quantity)
    SELECT v_meal_snack, dish_id, portion,
           dish_calories_per_100, dish_carbs_per_100,
           dish_protein_per_100, dish_fats_per_100,
           dish_carbs_per_100 / 12.0, 1
    FROM dish, (VALUES (v_dish_cottage, 150), (v_dish_almonds, 30)) AS t(d_id, portion)
    WHERE dish.dish_id = t.d_id;

    -- Завтрак (вчера): домашний суп 300 г + хлеб 30 г + яйцо 60 г
    INSERT INTO meal_item (meal_id, dish_id, meal_item_portion_grams,
                           meal_item_calories_per_100, meal_item_carbs_per_100,
                           meal_item_protein_per_100, meal_item_fats_per_100,
                           meal_item_carbs_per_100_bread_unit, meal_item_quantity)
    SELECT v_meal_y_breakfast, dish_id, portion,
           dish_calories_per_100, dish_carbs_per_100,
           dish_protein_per_100, dish_fats_per_100,
           dish_carbs_per_100 / 12.0, 1
    FROM dish, (VALUES
        (v_dish_homemade_soup, 300),
        (v_dish_bread_rye,      30),
        (v_dish_egg,            60)
    ) AS t(d_id, portion)
    WHERE dish.dish_id = t.d_id;

    -- Обед (вчера): лосось 180 г + брокколи 200 г + оливковое масло 10 г + хлеб 30 г
    INSERT INTO meal_item (meal_id, dish_id, meal_item_portion_grams,
                           meal_item_calories_per_100, meal_item_carbs_per_100,
                           meal_item_protein_per_100, meal_item_fats_per_100,
                           meal_item_carbs_per_100_bread_unit, meal_item_quantity)
    SELECT v_meal_y_lunch, dish_id, portion,
           dish_calories_per_100, dish_carbs_per_100,
           dish_protein_per_100, dish_fats_per_100,
           dish_carbs_per_100 / 12.0, 1
    FROM dish, (VALUES
        (v_dish_salmon,    180),
        (v_dish_brokkoli,  200),
        (v_dish_olive,      10),
        (v_dish_bread_rye,  30)
    ) AS t(d_id, portion)
    WHERE dish.dish_id = t.d_id;

    -- Ужин (вчера): куриная грудка 150 г + авокадо 80 г + помидор 100 г
    INSERT INTO meal_item (meal_id, dish_id, meal_item_portion_grams,
                           meal_item_calories_per_100, meal_item_carbs_per_100,
                           meal_item_protein_per_100, meal_item_fats_per_100,
                           meal_item_carbs_per_100_bread_unit, meal_item_quantity)
    SELECT v_meal_dinner, dish_id, portion,
           dish_calories_per_100, dish_carbs_per_100,
           dish_protein_per_100, dish_fats_per_100,
           dish_carbs_per_100 / 12.0, 1
    FROM dish, (VALUES
        (v_dish_chicken, 150),
        (v_dish_avocado,  80),
        (v_dish_tomato,  100)
    ) AS t(d_id, portion)
    WHERE dish.dish_id = t.d_id;

    -- Пересчёт total_* как делает DiaryService.recomputeTotals
    UPDATE nutrition_day nd
    SET total_calories = sub.cal,
        total_carbs    = sub.car,
        total_protein  = sub.pro,
        total_fats     = sub.fat
    FROM (
        SELECT m.nutrition_day_id,
               COALESCE(SUM(mi.meal_item_calories_per_100 * mi.meal_item_portion_grams / 100.0 * mi.meal_item_quantity), 0) AS cal,
               COALESCE(SUM(mi.meal_item_carbs_per_100    * mi.meal_item_portion_grams / 100.0 * mi.meal_item_quantity), 0) AS car,
               COALESCE(SUM(mi.meal_item_protein_per_100  * mi.meal_item_portion_grams / 100.0 * mi.meal_item_quantity), 0) AS pro,
               COALESCE(SUM(mi.meal_item_fats_per_100     * mi.meal_item_portion_grams / 100.0 * mi.meal_item_quantity), 0) AS fat
        FROM meal m
        LEFT JOIN meal_item mi ON mi.meal_id = m.meal_id
        WHERE m.nutrition_day_id IN (v_day_today, v_day_yesterday, v_day_minus2)
        GROUP BY m.nutrition_day_id
    ) sub
    WHERE nd.nutrition_day_id = sub.nutrition_day_id;

    -- ============================================================
    -- 13. Инсулиновые дозы: SHORT перед каждым приёмом + LONG раз в сутки
    -- ============================================================
    INSERT INTO insulin_dose (patient_id, meal_id, insulin_dose_units, insulin_dose_type,
                              insulin_dose_note, insulin_dose_taken_at,
                              insulin_dose_created_at, insulin_dose_updated_at) VALUES
        (v_patient_orlov, v_meal_breakfast,   6.0, 'SHORT', 'Перед завтраком (по ХЕ)',
            NOW() - INTERVAL '6 hours', NOW(), NOW()),
        (v_patient_orlov, v_meal_lunch,       8.0, 'SHORT', 'Перед обедом',
            NOW() - INTERVAL '2 hours', NOW(), NOW()),
        (v_patient_orlov, NULL,              18.0, 'LONG',  'Базальная доза перед сном',
            (NOW() - INTERVAL '1 days')::TIMESTAMP, NOW(), NOW()),
        (v_patient_orlov, NULL,              18.0, 'LONG',  'Базальная доза перед сном',
            (NOW() - INTERVAL '2 days')::TIMESTAMP, NOW(), NOW()),
        (v_patient_orlov, v_meal_y_breakfast, 5.0, 'SHORT', 'Перед завтраком (мало ХЕ)',
            (NOW() - INTERVAL '1 days' - INTERVAL '15 hours')::TIMESTAMP, NOW(), NOW()),
        (v_patient_orlov, v_meal_y_lunch,     7.0, 'SHORT', 'Перед обедом',
            (NOW() - INTERVAL '1 days' - INTERVAL '10 hours')::TIMESTAMP, NOW(), NOW());

    -- ============================================================
    -- 14. Приёмы (различные статусы) для Орлова
    -- ============================================================
    -- 14.1 COMPLETED #1 — 30 дней назад, базовая консультация
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_meeting_phone, appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() - INTERVAL '30 days')::TIMESTAMP,
            'VIDEO', 'COMPLETED',
            'https://meet.example.com/orlov-2025-04-30', NULL,
            'Контроль гликемии, обсудить дозу базального инсулина.',
            TRUE, TRUE,
            (NOW() - INTERVAL '37 days')::TIMESTAMP, (NOW() - INTERVAL '30 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_completed_1;

    -- 14.2 COMPLETED #2 — 14 дней назад
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_meeting_phone, appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() - INTERVAL '14 days')::TIMESTAMP,
            'VIDEO', 'COMPLETED',
            'https://meet.example.com/orlov-2025-05-15', NULL,
            'Повторный визит после коррекции терапии.',
            TRUE, TRUE,
            (NOW() - INTERVAL '21 days')::TIMESTAMP, (NOW() - INTERVAL '14 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_completed_2;

    -- 14.3 CANCELED — 21 день назад, причина указана
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_reason,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() - INTERVAL '21 days')::TIMESTAMP,
            'VIDEO', 'CANCELED',
            NULL,
            'Пациент простудился и попросил перенести консультацию.',
            FALSE, FALSE,
            (NOW() - INTERVAL '28 days')::TIMESTAMP, (NOW() - INTERVAL '22 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_canceled;

    -- 14.4 NO_SHOW — 7 дней назад
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_reason,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() - INTERVAL '7 days')::TIMESTAMP,
            'VIDEO', 'NO_SHOW',
            'https://meet.example.com/orlov-noshow', NULL,
            TRUE, TRUE,
            (NOW() - INTERVAL '14 days')::TIMESTAMP, (NOW() - INTERVAL '7 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_no_show;

    UPDATE appointments
       SET appointment_reason = 'Пациент не вышел на связь, попыток связаться 2.'
     WHERE appointment_id = v_app_no_show;

    -- 14.5 CREATED #1 — будущий, инициирован пациентом (он подтвердил, ждём врача)
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() + INTERVAL '3 days')::TIMESTAMP + TIME '10:00',
            'VIDEO', 'CREATED',
            'Хочу обсудить результаты последнего HbA1c.',
            TRUE, FALSE,
            (NOW() - INTERVAL '1 days')::TIMESTAMP, (NOW() - INTERVAL '1 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_created_1;

    -- 14.6 CREATED #2 — будущий, инициирован врачом (врач подтвердил, ждём пациента)
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() + INTERVAL '5 days')::TIMESTAMP + TIME '11:00',
            'VIDEO', 'CREATED',
            'https://meet.example.com/orlov-followup',
            'Контроль через 2 недели после коррекции инсулина.',
            FALSE, TRUE,
            NOW()::TIMESTAMP, NOW()::TIMESTAMP)
    RETURNING appointment_id INTO v_app_created_2;

    -- 14.7 CONFIRMED — будущий, обе стороны подтвердили
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_meeting_phone,
                              appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_orlov, v_doctor_id,
            (NOW() + INTERVAL '10 days')::TIMESTAMP + TIME '14:00',
            'VIDEO', 'CONFIRMED',
            'https://meet.example.com/orlov-2025-06-10', '+7 (495) 555-78-90',
            'Принести дневник самоконтроля и результаты анализов.',
            TRUE, TRUE,
            (NOW() - INTERVAL '4 days')::TIMESTAMP, (NOW() - INTERVAL '2 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_confirmed;

    -- ============================================================
    -- 15. Приёмы для Анны (Смирновой) и Ивана (Петрова)
    -- ============================================================
    -- Анна — COMPLETED 25 дней назад
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_smirnova, v_doctor_id,
            (NOW() - INTERVAL '25 days')::TIMESTAMP,
            'VIDEO', 'COMPLETED',
            'https://meet.example.com/smirnova-2025-05-04',
            'Эпизод гипогликемии после спорта — обсудить базальную дозу.',
            TRUE, TRUE,
            (NOW() - INTERVAL '32 days')::TIMESTAMP, (NOW() - INTERVAL '25 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_smirnova_1;

    -- Анна — будущий CREATED, инициирован пациентом
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_smirnova, v_doctor_id,
            (NOW() + INTERVAL '2 days')::TIMESTAMP + TIME '15:00',
            'AUDIO', 'CREATED',
            'Низкий канал — выбрала аудиоконсультацию.',
            TRUE, FALSE,
            (NOW() - INTERVAL '6 hours')::TIMESTAMP, (NOW() - INTERVAL '6 hours')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_smirnova_2;

    -- Иван Петров — CONFIRMED через 4 дня
    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime,
                              appointment_consultation_type, appointment_status,
                              appointment_link, appointment_meeting_notes,
                              appointment_confirmed_by_patient, appointment_confirmed_by_doctor,
                              appointment_created_at, appointment_updated_at)
    VALUES (v_patient_petrov, v_doctor_id,
            (NOW() + INTERVAL '4 days')::TIMESTAMP + TIME '09:00',
            'VIDEO', 'CONFIRMED',
            'https://meet.example.com/petrov-checkin',
            'Плановый контроль HbA1c.',
            TRUE, TRUE,
            (NOW() - INTERVAL '5 days')::TIMESTAMP, (NOW() - INTERVAL '4 days')::TIMESTAMP)
    RETURNING appointment_id INTO v_app_petrov_1;

    -- ============================================================
    -- 16. Записи в медкарту: для COMPLETED + одна "первичная" без приёма
    -- ============================================================
    -- 16.1 Орлов: первичная запись (без appointment_id) — анамнез на дебют СД
    INSERT INTO medical_record (patient_id, doctor_id, appointment_id,
                                record_title, record_complaints,
                                record_anamnesis_morbi, record_anamnesis_vitae,
                                record_objective_status,
                                record_diagnosis,
                                record_examination_plan, record_recommendations,
                                record_prescriptions,
                                record_next_visit_date,
                                record_created_at, record_updated_at)
    VALUES (v_patient_orlov, v_doctor_id, NULL,
            'Первичная запись (импорт из бумажной карты)',
            'Жажда, частое мочеиспускание, утомляемость в течение последних 3 месяцев.',
            'СД 2 типа выявлен в сентябре 2018 г. Глюкоза натощак 11.4 ммоль/л, HbA1c 8.9%. Назначена терапия метформином, к 2022 году добавлен базальный инсулин.',
            'Из перенесённых: ОРВИ, аппендэктомия в 2003. Наследственность отягощена: мать — СД-2.',
            'Состояние удовлетворительное. ИМТ 29.2 (избыточная масса тела). Стопы без язв. АД 130/85.',
            'Сахарный диабет 2 типа на комбинированной терапии (метформин + базальный инсулин гларгин). Диабетическая ретинопатия I ст. справа.',
            'HbA1c 1 раз в 3 мес.; ОАК, биохимия (липидограмма, креатинин); микроальбумин в моче; осмотр офтальмолога.',
            'Стол №9. Контроль гликемии 4 раза в день, ведение электронного дневника.',
            'Метформин 1000 мг × 2 р/сут утром и вечером. Гларгин 18 ЕД п/к перед сном. Аторвастатин 20 мг вечером.',
            CURRENT_DATE + 30,
            (NOW() - INTERVAL '60 days')::TIMESTAMP, (NOW() - INTERVAL '60 days')::TIMESTAMP)
    RETURNING record_id INTO v_record_general;

    -- 16.2 Орлов: запись по приёму COMPLETED #1
    INSERT INTO medical_record (patient_id, doctor_id, appointment_id,
                                record_title, record_complaints,
                                record_anamnesis_morbi,
                                record_objective_status,
                                record_local_status,
                                record_diagnosis,
                                record_examination_plan, record_recommendations,
                                record_prescriptions,
                                record_next_visit_date,
                                record_created_at, record_updated_at)
    VALUES (v_patient_orlov, v_doctor_id, v_app_completed_1,
            'Плановый контроль СД, коррекция базального инсулина',
            'Утренние гликемии натощак выше целевого: 7.5–8.2 ммоль/л.',
            'HbA1c за 3 месяца снизился с 8.0 до 7.4%. Диета соблюдается, активность — пешие прогулки 4 раза в неделю.',
            'Удовлетворительное. ИМТ 28.9. АД 132/82. ЧСС 76.',
            'Стопы: язв нет, чувствительность сохранена. Места инъекций без липодистрофии.',
            'Сахарный диабет 2 типа, на комбинированной терапии. Целевые значения HbA1c < 7%.',
            'HbA1c через 3 мес. Микроальбумин в моче через 6 мес. Контроль АД 2 раза в день.',
            'Увеличить базальный гларгин с 16 до 18 ЕД. Сохранить метформин. Включить ходьбу 60 мин ≥ 5 раз в неделю.',
            'Гларгин 18 ЕД п/к перед сном. Метформин 1000 мг × 2. Аторвастатин 20 мг вечером.',
            CURRENT_DATE - 14,
            (NOW() - INTERVAL '30 days')::TIMESTAMP, (NOW() - INTERVAL '30 days')::TIMESTAMP)
    RETURNING record_id INTO v_record_app_1;

    -- 16.3 Орлов: запись по приёму COMPLETED #2
    INSERT INTO medical_record (patient_id, doctor_id, appointment_id,
                                record_title, record_complaints,
                                record_anamnesis_morbi,
                                record_objective_status,
                                record_diagnosis,
                                record_examination_plan, record_recommendations,
                                record_prescriptions,
                                record_next_visit_date,
                                record_created_at, record_updated_at)
    VALUES (v_patient_orlov, v_doctor_id, v_app_completed_2,
            'Повторный контроль после коррекции дозы',
            'Жалоб нет. Самочувствие улучшилось.',
            'После увеличения гларгина до 18 ЕД утренние гликемии вернулись в диапазон 5.6–6.8 ммоль/л.',
            'Удовлетворительное. ИМТ 28.5 (динамика положительная).',
            'СД 2 типа, компенсация улучшается. Целевой диапазон HbA1c < 7% — близко.',
            'HbA1c через 6 нед. Биохимия (липиды) через 3 мес.',
            'Сохранять текущую терапию. Продолжать ходьбу + добавить силовые 1–2 раза/нед.',
            'Без изменений. Гларгин 18 ЕД, Метформин 1000 мг × 2, Аторвастатин 20 мг.',
            CURRENT_DATE + 30,
            (NOW() - INTERVAL '14 days')::TIMESTAMP, (NOW() - INTERVAL '14 days')::TIMESTAMP)
    RETURNING record_id INTO v_record_app_2;

    -- 16.4 Анна Смирнова: запись по её COMPLETED
    INSERT INTO medical_record (patient_id, doctor_id, appointment_id,
                                record_title, record_complaints,
                                record_anamnesis_morbi,
                                record_objective_status,
                                record_diagnosis,
                                record_recommendations,
                                record_prescriptions,
                                record_next_visit_date,
                                record_created_at, record_updated_at)
    VALUES (v_patient_smirnova, v_doctor_id, v_app_smirnova_1,
            'Эпизод гипогликемии после физической нагрузки',
            'Эпизод гипогликемии 2.8 ммоль/л через 30 мин после йоги.',
            'СД 1 типа с 2014 г., базис-болюсная терапия. До эпизода — стабильная компенсация.',
            'Состояние удовлетворительное. ИМТ 21.4.',
            'СД 1 типа, лёгкая гипогликемия после нагрузки.',
            'Перед физнагрузкой проверять гликемию; если < 5.5 — съесть 1 ХЕ. Снизить базальный деглудек на 2 ЕД в дни с тренировками.',
            'Аспарт 6–10 ЕД перед едой. Деглудек 22 ЕД (было 24).',
            CURRENT_DATE + 30,
            (NOW() - INTERVAL '25 days')::TIMESTAMP, (NOW() - INTERVAL '25 days')::TIMESTAMP)
    RETURNING record_id INTO v_record_smirnova;

    -- ============================================================
    -- 17. Коды МКБ-10 в записях медкарты (через join-таблицу)
    -- ============================================================
    INSERT INTO medical_record_icd10_codes (record_id, icd10_code) VALUES
        (v_record_general, 'E11.9'),
        (v_record_general, 'E11.3'),  -- ретинопатия
        (v_record_app_1,   'E11.9'),
        (v_record_app_2,   'E11.9'),
        (v_record_smirnova,'E10.9'),
        (v_record_smirnova,'E16.2'); -- гипогликемия

    -- ============================================================
    -- 18. Отзывы для COMPLETED-приёмов
    -- ============================================================
    INSERT INTO reviews (appointment_id, patient_id, doctor_id, rating, comment, created_at) VALUES
        (v_app_completed_1, v_patient_orlov, v_doctor_id, 5,
         'Очень внимательный врач, объяснил, как корректировать базальный инсулин. Чувствую улучшение уже через неделю.',
         (NOW() - INTERVAL '29 days')::TIMESTAMP),
        (v_app_completed_2, v_patient_orlov, v_doctor_id, 4,
         'Спасибо, всё по делу. Хочется ещё больше про диету услышать в следующий раз.',
         (NOW() - INTERVAL '13 days')::TIMESTAMP),
        (v_app_smirnova_1,  v_patient_smirnova, v_doctor_id, 5,
         'Алексей Иванович сразу понял проблему с гипо после тренировок и подсказал решение.',
         (NOW() - INTERVAL '24 days')::TIMESTAMP);

    -- ============================================================
    -- 19. Reminder и Notification для Орлова
    -- ============================================================
    INSERT INTO reminder (patient_id, reminder_kind, reminder_title, reminder_message,
                          reminder_recurrence_time, reminder_recurrence_days,
                          reminder_enabled, reminder_created_at, reminder_updated_at)
    VALUES (v_patient_orlov, 'RECURRING',
            'Базальный инсулин',
            'Гларгин 18 ЕД п/к перед сном.',
            TIME '22:30',
            'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY',
            TRUE, NOW(), NOW())
    RETURNING reminder_id INTO v_reminder_recur;

    INSERT INTO reminder (patient_id, reminder_kind, reminder_title, reminder_message,
                          reminder_scheduled_at,
                          reminder_enabled, reminder_created_at, reminder_updated_at)
    VALUES (v_patient_orlov, 'ONE_SHOT',
            'Сдать кровь на HbA1c',
            'Записаться в лабораторию, направление от Кузнецова.',
            (NOW() + INTERVAL '5 days'),
            TRUE, NOW(), NOW())
    RETURNING reminder_id INTO v_reminder_once;

    INSERT INTO notification (user_id, reminder_id, notification_title, notification_message,
                              notification_type, notification_is_read, notification_created_at) VALUES
        (v_patient_orlov, NULL,
         'Запись на приём подтверждена',
         'Кузнецов А.И. подтвердил вашу запись через 10 дней (видео).',
         'APPOINTMENT_CONFIRMED', FALSE, NOW() - INTERVAL '2 days'),
        (v_patient_orlov, NULL,
         'Напоминание о приёме',
         'Завтра в 14:00 — видеоконсультация с Кузнецовым А.И.',
         'APPOINTMENT_REMINDER', TRUE,  NOW() - INTERVAL '9 days'),
        (v_patient_orlov, v_reminder_recur,
         'Базальный инсулин',
         'Гларгин 18 ЕД п/к перед сном.',
         'REMINDER', FALSE, NOW() - INTERVAL '1 hour'),
        (v_doctor_id, NULL,
         'Новая запись на приём',
         'Орлов С.М. записался на видеоконсультацию через 3 дня.',
         'APPOINTMENT_NEW', FALSE, NOW() - INTERVAL '1 days'),
        (v_doctor_id, NULL,
         'Новый отзыв',
         'Пациент Орлов С.М. поставил оценку 5/5.',
         'REVIEW_NEW', TRUE, NOW() - INTERVAL '13 days');

    -- ============================================================
    -- 20. Чат между Орловым и Кузнецовым с историей переписки
    -- ============================================================
    INSERT INTO chats DEFAULT VALUES RETURNING chat_id INTO v_chat_id;

    INSERT INTO chat_participants (chat_id, user_id) VALUES
        (v_chat_id, v_patient_orlov),
        (v_chat_id, v_doctor_id);

    INSERT INTO chat_messages (chat_id, sender_id, content, sent_at) VALUES
        (v_chat_id, v_patient_orlov,
         'Здравствуйте, Алексей Иванович! После увеличения гларгина утренний сахар стал 5.8–6.5. Спасибо!',
         NOW() - INTERVAL '12 days'),
        (v_chat_id, v_doctor_id,
         'Отлично, держим эту дозу. Не забудьте сдать HbA1c через 6 недель — направление в чате.',
         NOW() - INTERVAL '12 days' + INTERVAL '15 minutes'),
        (v_chat_id, v_patient_orlov,
         'Понял. А можно при тренировке (силовая 40 мин) колоть короткий по обычной схеме?',
         NOW() - INTERVAL '6 days'),
        (v_chat_id, v_doctor_id,
         'Если глюкоза до тренировки 5.5–8 — короткий минус 1–2 ЕД от расчётной дозы по ХЕ. При <5.5 — съешьте 1 ХЕ и не колите.',
         NOW() - INTERVAL '6 days' + INTERVAL '20 minutes'),
        (v_chat_id, v_patient_orlov,
         'Спасибо, попробую. Чувствую слабость к вечеру, может уменьшить дозу метформина?',
         NOW() - INTERVAL '2 days'),
        (v_chat_id, v_doctor_id,
         'Слабость может быть от метформина, но сначала проверим витамин B12 и креатинин — назначу на следующем приёме.',
         NOW() - INTERVAL '2 days' + INTERVAL '1 hour');

    -- ============================================================
    -- ИТОГ
    -- ============================================================
    RAISE NOTICE 'V33: seed-данные загружены. Доктор: a.kuznetsov@telemed.ru / password.';
    RAISE NOTICE 'V33: витринный пациент: sergey.orlov@example.com / password.';
END $$;
