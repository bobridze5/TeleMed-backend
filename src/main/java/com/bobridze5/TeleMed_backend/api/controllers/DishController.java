package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.service.dish.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.DISH)
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @PostMapping
    public DishResponse createDish(
            @RequestBody @Valid CreateDishRequest request,
            @AuthenticationPrincipal User user
    ) {
        // TODO: 201 location
        return dishService.createDish(request, user.getId());
    }

    @GetMapping("/{dishId}")
    public DishResponse getDishById(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal User user
    ) {
        return dishService.getDishById(id, user.getId());
    }

    @GetMapping
    public Page<DishResponse> getDishes(
            @ModelAttribute DishFilterRequest request,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 20) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return dishService.getDishes(request, user.getId(), pageable);
    }

    @PatchMapping("/{dishId}")
    public DishResponse changeDish(
            @PathVariable("dishId") Long id,
            @RequestBody @Valid UpdateDishRequest request,
            @AuthenticationPrincipal User user

    ) {
        return dishService.changeDish(request, id, user.getId());
    }

    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDish(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal User user
    ) {
        dishService.deleteDish(id, user.getId());
    }
}
