package com.bobridze5.TeleMed_backend.core.service.dish;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;

public interface DishService {
    DishResponse createDish(DishRequest request, Long userId);
    DishResponse getDishById(Long dishId, Long userId);

}
