package com.bobridze5.TeleMed_backend.core.service.dish;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishRequest;
import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;
import com.bobridze5.TeleMed_backend.api.mappers.DishMapper;
import com.bobridze5.TeleMed_backend.core.entity.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.DishRepository;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final DishRepository dishRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public DishResponse createDish(DishRequest request, Long userId) {
        Dish dish = dishMapper.mapToEntity(request);

        Optional<Patient> patient = patientRepository.findByUserId(userId);
        patient.ifPresent(dish::setPatient);

        return dishMapper.mapToResponse(dishRepository.save(dish));
    }

    @Override
    /*
    Еда бывает своя и чужая. Чужую еду мы не видим, свою видеть должны
     */
    public DishResponse getDishById(Long dishId, Long userId) {

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент не найден"));

        if (!dish.getPatient().getId().equals(patient.getId())) {
            throw new AccessDeniedException("Доступ запрещён");
        }


        return dishMapper.mapToResponse(dish);
    }
}
