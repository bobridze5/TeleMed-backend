CREATE TABLE dish (
    dish_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    dish_name VARCHAR(255) NOT NULL,
    dish_calories_per_100 DOUBLE PRECISION NOT NULL,
    dish_carbs_per_100 DOUBLE PRECISION NOT NULL,
    dish_protein_per_100 DOUBLE PRECISION NOT NULL,
    dish_fats_per_100 DOUBLE PRECISION NOT NULL,
    dish_photo_url VARCHAR(512),
    dish_description TEXT,
    CONSTRAINT fk_dish_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE SET NULL,
    CONSTRAINT dish_values_nonnegative CHECK (
        dish_calories_per_100 >= 0
        AND dish_carbs_per_100 >= 0
        AND dish_protein_per_100 >= 0
        AND dish_fats_per_100 >= 0
    )
);

CREATE INDEX idx_dish_patient_id ON dish (patient_id);

CREATE TABLE nutrition_day (
    nutrition_day_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    diary_date DATE NOT NULL,
    total_calories DOUBLE PRECISION,
    total_carbs DOUBLE PRECISION,
    total_protein DOUBLE PRECISION,
    total_fats DOUBLE PRECISION,
    notes TEXT,
    completed BOOLEAN,
    CONSTRAINT fk_nutrition_day_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT uk_patient_day UNIQUE (patient_id, diary_date)
);

CREATE INDEX idx_patient_day ON nutrition_day (patient_id, diary_date);

CREATE TABLE meal (
    meal_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    meal_datetime TIMESTAMP WITHOUT TIME ZONE,
    meal_type VARCHAR(50) NOT NULL,
    nutrition_day_id BIGINT,
    CONSTRAINT fk_meal_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_nutrition_day FOREIGN KEY (nutrition_day_id) REFERENCES nutrition_day (nutrition_day_id) ON DELETE CASCADE
);

CREATE INDEX idx_meal_patient_id ON meal (patient_id);

CREATE INDEX idx_meal_nutrition_day_id ON meal (nutrition_day_id);

CREATE TABLE meal_item (
    meal_item_id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    meal_item_portion_grams DOUBLE PRECISION NOT NULL,
    meal_item_calories_per_100 DOUBLE PRECISION NOT NULL,
    meal_item_carbs_per_100 DOUBLE PRECISION NOT NULL,
    meal_item_protein_per_100 DOUBLE PRECISION NOT NULL,
    meal_item_fats_per_100 DOUBLE PRECISION NOT NULL,
    meal_item_carbs_per_100_bread_unit DOUBLE PRECISION NOT NULL,
    meal_item_quantity INTEGER NOT NULL,
    CONSTRAINT fk_meal_item_meal FOREIGN KEY (meal_id) REFERENCES meal (meal_id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_item_dish FOREIGN KEY (dish_id) REFERENCES dish (dish_id) ON DELETE RESTRICT,
    CONSTRAINT ck_meal_item_nutrition_nonneg CHECK (
        meal_item_portion_grams >= 0
        AND meal_item_calories_per_100 >= 0
        AND meal_item_carbs_per_100 >= 0
        AND meal_item_protein_per_100 >= 0
        AND meal_item_fats_per_100 >= 0
        AND meal_item_carbs_per_100_bread_unit >= 0
        AND meal_item_quantity > 0
    )
);

CREATE INDEX idx_meal_item_meal_id ON meal_item (meal_id);

CREATE INDEX idx_meal_item_dish_id ON meal_item (dish_id);