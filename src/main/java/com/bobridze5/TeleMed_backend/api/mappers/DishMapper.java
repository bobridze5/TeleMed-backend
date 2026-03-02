package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;

public interface DishMapper extends
        ToEntityMapper<DishRequest, Dish>,
        ToResponseMapper<DishResponse, Dish>
{

}
