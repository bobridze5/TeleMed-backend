package com.bobridze5.TeleMed_backend.core.service.utils;

public final class Constant {
    public static class Nutrition {
        public static final Double GRAMS_PER_BREAD_UNIT = 12.0;

        public static Double toBreadUnits(Double carbs) {
            if (carbs == null) return null;

            return Math.round(carbs / GRAMS_PER_BREAD_UNIT * 10.0) / 10.0;
        }
    }
}
