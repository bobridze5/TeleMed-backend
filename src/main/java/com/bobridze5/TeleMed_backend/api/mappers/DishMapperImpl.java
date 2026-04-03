package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import org.springframework.stereotype.Component;

@Component
public class DishMapperImpl implements DishMapper {

    @Override
    public Dish mapCreateRequest(CreateDishRequest request) {
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
//                .status(DishStatus.DRAFT)
                .build();
    }

    @Override
    public void updateEntity(UpdateDishRequest request, Dish dish) {
        if (request == null) return;

        if (request.name() != null) dish.setName(request.name());
        if (request.calories() != null) dish.setCalories(request.calories());
        if (request.fats() != null) dish.setFats(request.fats());
        if (request.carbs() != null) dish.setCarbs(request.carbs());
        if (request.description() != null) dish.setDescription(request.description());
        if (request.photoURL() != null) dish.setPhotoURL(request.photoURL());
    }

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
                dish.getDescription(),
                dish.getStatus()
        );
    }
}
