package com.bobridze5.TeleMed_backend.api.controllers.patient.params;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.service.dish.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.DISH)
@RequiredArgsConstructor
@Tag(name = "Блюда", description = "Управление справочником блюд для трекинга питания")
public class DishController {
    private final DishService dishService;

    @PostMapping
    @Operation(summary = "Создать блюдо", description = "Добавляет новое блюдо в справочник текущего пользователя")
    public DishResponse createDish(
            @RequestBody @Valid CreateDishRequest request,
            @AuthenticationPrincipal User user
    ) {
        // TODO: 201 location
        return dishService.createDish(request, user.getId());
    }

    @GetMapping("/{dishId}")
    @Operation(summary = "Получить блюдо по ID")
    public DishResponse getDishById(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal User user
    ) {
        return dishService.getDishById(id, user.getId());
    }

    @GetMapping
    @Operation(summary = "Получить список блюд", description = "Возвращает страницу блюд с фильтрацией по названию и нутриентам")
    public Page<DishResponse> getDishes(
            @ParameterObject @ModelAttribute DishFilterRequest request,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 20) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return dishService.getDishes(request, user.getId(), pageable);
    }

    @PatchMapping("/{dishId}")
    @Operation(summary = "Обновить блюдо")
    public DishResponse changeDish(
            @PathVariable("dishId") Long id,
            @RequestBody @Valid UpdateDishRequest request,
            @AuthenticationPrincipal User user

    ) {
        return dishService.changeDish(request, id, user.getId());
    }

    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить блюдо")
    public void deleteDish(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal User user
    ) {
        dishService.deleteDish(id, user.getId());
    }
}
