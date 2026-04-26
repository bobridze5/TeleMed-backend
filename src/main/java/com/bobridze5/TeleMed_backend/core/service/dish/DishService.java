package com.bobridze5.TeleMed_backend.core.service.dish;

import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.api.mappers.DishMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.Dish;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.DishRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import com.bobridze5.TeleMed_backend.core.service.utils.DishSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishService {
    private final DishMapper dishMapper;
    private final DishRepository dishRepository;
    private final PatientRepository patientRepository;

    @Value("${app.upload.dir:uploads/dish-images}")
    private String uploadDir;

    @Transactional
    public DishResponse createDish(CreateDishRequest request, Long userId) {
        Dish dish = dishMapper.mapCreateRequest(request);

        Optional<Patient> patient = patientRepository.findById(userId);
        patient.ifPresent(dish::setPatient);

        return dishMapper.mapToResponse(dishRepository.save(dish));
    }

    @Transactional(readOnly = true)
    public DishResponse getDishById(Long dishId, Long userId) {
        Dish dish = dishRepository.findDish(dishId, userId)
                .orElseThrow(() -> {
                    log.warn("Попытка получить блюдо: доступ запрещён либо блюдо не найдено: dishId = {}, userId = {}", dishId, userId);
                    return new EntityNotFoundException("Блюдо не найдено");
                });

        return dishMapper.mapToResponse(dish);
    }

    @Transactional(readOnly = true)
    public Page<DishResponse> getDishes(DishFilterRequest request, Long userId, Pageable pageable) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));

        Specification<Dish> spec = DishSpecification.build(request, patient.getId());

        return dishRepository.findAll(spec, pageable).map(dishMapper::mapToResponse);
    }

    @Transactional
    public DishResponse changeDish(UpdateDishRequest request, Long dishId, Long userId) {
        Dish dish = dishRepository.findDish(dishId, userId)
                .orElseThrow(() -> {
                    log.warn("Попытка изменить блюдо: доступ запрещён либо блюдо не найдено: dishId = {}, userId = {}", dishId, userId);
                    return new EntityNotFoundException("Блюдо не найдено");
                });

        dishMapper.updateEntity(request, dish);

        return dishMapper.mapToResponse(dish);
    }

    @Transactional
    public void deleteDish(Long dishId, Long userId) {
        Dish dish = dishRepository.findDish(dishId, userId)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить блюдо: доступ запрещён либо блюдо не найдено: dishId = {}, userId = {}", dishId, userId);
                    return new EntityNotFoundException("Блюдо не найдено");
                });

        dishRepository.delete(dish);
    }

    @Transactional
    public DishResponse uploadImage(Long dishId, Long userId, MultipartFile file) throws IOException {
        Dish dish = dishRepository.findDish(dishId, userId)
                .orElseThrow(() -> {
                    log.warn("Попытка получить блюдо: доступ запрещён либо блюдо не найдено: dishId = {}, userId = {}", dishId, userId);
                    return new EntityNotFoundException("Блюдо не найдено");
                });

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".jpg";

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        Path dest = dir.resolve(dishId + ext);
        file.transferTo(dest);

        dish.setPhotoURL("/api/v1/dishes/" + dishId + "/image");
        return dishMapper.mapToResponse(dishRepository.save(dish));
    }

    public byte[] getImage(Long dishId) throws IOException {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        if (dish.getPhotoURL() == null) {
            throw new EntityNotFoundException("Изображение не найдено");
        }

        Path dir = Paths.get(uploadDir);
        String[] exts = {".jpg", ".jpeg", ".png", ".webp", ".gif"};
        for (String ext : exts) {
            Path candidate = dir.resolve(dishId + ext);
            if (Files.exists(candidate)) {
                return Files.readAllBytes(candidate);
            }
        }
        throw new EntityNotFoundException("Файл изображения не найден");
    }
}
