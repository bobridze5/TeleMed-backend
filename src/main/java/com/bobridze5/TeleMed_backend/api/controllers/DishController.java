package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.service.interfaces.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @PostMapping
    public ResponseEntity<DishResponse> createDish(
            @RequestBody @Valid DishRequest request,
            @AuthenticationPrincipal User user
    ) {
        DishResponse response = dishService.createDish(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{dishId}")
    public ResponseEntity<DishResponse> getDishById(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal User user
    ) {
        DishResponse response = dishService.getDishById(id, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getDishes() {
        return null;
    }

    @PatchMapping("/{dishId}")
    public ResponseEntity<?> changeDishParams() {
        return null;
    }

    @PutMapping("/{dishId}")
    public ResponseEntity<?> changeDish() {
        return null;
    }

    @DeleteMapping("/{dishId}")
    public ResponseEntity<?> deleteDish() {
        return null;
    }
}
