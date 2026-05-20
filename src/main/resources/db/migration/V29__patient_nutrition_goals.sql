-- Дневные нутри-цели пациента (выставляются им же в профиле).
-- Все nullable: пациент может не задавать норму — тогда дневник просто не
-- сравнивает текущие totals с целью. Положительные значения — но constraint
-- здесь не ставим, валидация на уровне DTO (@Positive). DOUBLE PRECISION
-- симметрично существующим nutri-полям (см. patient_hba1c, dish.calories).
ALTER TABLE patients
    ADD COLUMN patient_daily_calories_goal INTEGER,
    ADD COLUMN patient_daily_protein_goal  DOUBLE PRECISION,
    ADD COLUMN patient_daily_fats_goal     DOUBLE PRECISION,
    ADD COLUMN patient_daily_carbs_goal    DOUBLE PRECISION;
