package com.bobridze5.TeleMed_backend.api.controllers.patient.eat;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return dishService.createDish(request, userDetails.getUserId());
    }

    @GetMapping("/{dishId}")
    @Operation(summary = "Получить блюдо по ID")
    public DishResponse getDishById(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return dishService.getDishById(id, userDetails.getUserId());
    }

    @GetMapping
    @Operation(summary = "Получить список блюд", description = "Возвращает страницу блюд с фильтрацией по названию и нутриентам")
    public Page<DishResponse> getDishes(
            @ParameterObject @ModelAttribute DishFilterRequest request,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 20) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return dishService.getDishes(request, userDetails.getUserId(), pageable);
    }

    @PatchMapping("/{dishId}")
    @Operation(summary = "Обновить блюдо")
    public DishResponse changeDish(
            @PathVariable("dishId") Long id,
            @RequestBody @Valid UpdateDishRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return dishService.changeDish(request, id, userDetails.getUserId());
    }

    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить блюдо")
    public void deleteDish(
            @PathVariable("dishId") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        dishService.deleteDish(id, userDetails.getUserId());
    }

    @PostMapping(value = "/{dishId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить фото блюда")
    public DishResponse uploadImage(
            @PathVariable("dishId") Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        return dishService.uploadImage(id, userDetails.getUserId(), file);
    }

    @GetMapping("/{dishId}/image")
    @Operation(summary = "Получить фото блюда")
    public ResponseEntity<byte[]> getImage(
            @PathVariable("dishId") Long id
    ) throws IOException {
        byte[] data = dishService.getImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(data);
    }
}
