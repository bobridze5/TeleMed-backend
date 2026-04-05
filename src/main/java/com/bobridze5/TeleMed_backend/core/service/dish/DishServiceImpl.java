package com.bobridze5.TeleMed_backend.core.service.dish;

import com.bobridze5.TeleMed_backend.api.dto.dish.CreateDishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.dto.dish.UpdateDishRequest;
import com.bobridze5.TeleMed_backend.api.mappers.DishMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import com.bobridze5.TeleMed_backend.core.entity.report.DishStatus;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.DishRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import com.bobridze5.TeleMed_backend.core.service.utils.DishSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final DishRepository dishRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public DishResponse createDish(CreateDishRequest request, Long userId) {
        Dish dish = dishMapper.mapCreateRequest(request);
        dish.setStatus(DishStatus.DRAFT);

        Optional<Patient> patient = patientRepository.findById(userId);
        patient.ifPresent(dish::setPatient);

        return dishMapper.mapToResponse(dishRepository.save(dish));
    }

    @Override
    /*
    Еда бывает своя и чужая. Чужую еду мы не видим, свою видеть должны
     */
    @Transactional(readOnly = true)
    public DishResponse getDishById(Long dishId, Long userId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));

        if (!patient.canView(dish)) {
            log.warn("Пользователь id = {} попытался получить доступ блюду dishId = {}", userId, dishId);
            throw new EntityNotFoundException("Блюдо не найдено");
        }

        return dishMapper.mapToResponse(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DishResponse> getDishes(DishFilterRequest request, Long userId, Pageable pageable) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));

        Specification<Dish> spec = DishSpecification.build(request, patient.getId());

        return dishRepository.findAll(spec, pageable).map(dishMapper::mapToResponse);
    }

    @Override
    @Transactional
    public DishResponse changeDish(UpdateDishRequest request, Long dishId, Long userId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));

        if (!patient.isOwner(dish)) {
            log.warn("Пользователь id = {} попытался изменить блюдо id = {}", userId, dishId);
            throw new EntityNotFoundException("Блюдо не найдено");
        }

        dishMapper.updateEntity(request, dish);

        return dishMapper.mapToResponse(dish);
    }

    @Override
    @Transactional
    public void deleteDish(Long dishId, Long userId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));

        if (!patient.isOwner(dish)) {
            log.warn("Пользователь id = {} попытался удалить блюдо id = {}", userId, dishId);
            throw new EntityNotFoundException("Блюдо не найдено");
        }

        dishRepository.delete(dish);
    }
}
