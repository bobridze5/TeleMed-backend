package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import org.springframework.stereotype.Component;

@Component
public class DishMapperImpl implements DishMapper {

    @Override
    public DishResponse mapToResponse(Dish dish) {
        return new DishResponse(
                dish.getId(),
                dish.getName(),
                dish.getCalories(),
                dish.getCarbs(),
                dish.getProtein(),
                dish.getFats(),
                dish.getPhotoURL(),
                dish.getDescription()
        );
    }

    @Override
    public Dish mapToEntity(DishRequest request) {
        return Dish.builder()
                .id(null)
                .patient(null)
                .name(request.name())
                .calories(request.calories())
                .carbs(request.carbs())
                .protein(request.protein())
                .fats(request.fats())
                .photoURL(request.photoURL())
                .description(request.description())
                .build();
    }

}
