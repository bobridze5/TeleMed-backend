package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Dish;
import org.springframework.stereotype.Component;

import static com.bobridze5.TeleMed_backend.core.service.utils.Constant.Nutrition;

@Component
public class DishMapper {

    public Dish mapCreateRequest(CreateDishRequest request) {
        return Dish.builder()
                .name(request.name())
                .calories(request.calories())
                .carbs(request.carbs())
                .protein(request.protein())
                .fats(request.fats())
                .photoURL(request.photoURL())
                .description(request.description())
                .build();
    }

    public void updateEntity(UpdateDishRequest request, Dish dish) {
        if (request == null) return;

        if (request.name() != null) dish.setName(request.name());
        if (request.calories() != null) dish.setCalories(request.calories());
        if (request.fats() != null) dish.setFats(request.fats());
        if (request.carbs() != null) dish.setCarbs(request.carbs());
        if (request.protein() != null) dish.setProtein(request.protein());
        if (request.description() != null) dish.setDescription(request.description());
        if (request.photoURL() != null) dish.setPhotoURL(request.photoURL());
    }

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
                Nutrition.toBreadUnits(dish.getCarbs()),
                dish.getPatient() != null ? dish.getPatient().getId() : null
        );
    }
}
