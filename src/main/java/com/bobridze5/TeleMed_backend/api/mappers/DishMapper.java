package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;

public interface DishMapper {
    Dish mapCreateRequest(CreateDishRequest request);

    void updateEntity(UpdateDishRequest request, Dish dish);

    DishResponse mapToResponse(Dish dish);
}
