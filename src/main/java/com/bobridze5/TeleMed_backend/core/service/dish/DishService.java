package com.bobridze5.TeleMed_backend.core.service.dish;

import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DishService {
    DishResponse createDish(CreateDishRequest request, Long userId);

    DishResponse getDishById(Long dishId, Long userId);

    Page<DishResponse> getDishes(DishFilterRequest request, Long userId, Pageable pageable);

    DishResponse changeDish(UpdateDishRequest request, Long dishId, Long userId);

    void deleteDish(Long dishId, Long userId);

}
