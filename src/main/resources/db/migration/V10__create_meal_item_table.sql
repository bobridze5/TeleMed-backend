CREATE TABLE "meal_item" (
    meal_item_id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    meal_item_portion_grams DOUBLE PRECISION NOT NULL,
    meal_item_notes TEXT,

    -- Ограничение на положительный вес
    CONSTRAINT check_meal_item_portion_grams CHECK (meal_item_portion_grams >= 0),

    -- Связь с таблицей приемов пищи
    CONSTRAINT fk_meal_item_meal
        FOREIGN KEY (meal_id)
        REFERENCES meal (meal_id)
        ON DELETE CASCADE,

    -- Связь с таблицей блюд
    CONSTRAINT fk_meal_item_dish
        FOREIGN KEY (dish_id)
        REFERENCES dish (dish_id)
        ON DELETE RESTRICT
);

-- Индексы для ускорения джоинов
CREATE INDEX idx_meal_item_meal_id ON meal_item(meal_id);
CREATE INDEX idx_meal_item_dish_id ON meal_item(dish_id);