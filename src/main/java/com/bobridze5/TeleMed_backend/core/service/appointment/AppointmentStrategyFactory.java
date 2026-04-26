package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentStrategyFactory {
    private final List<AppointmentCreationStrategy> strategies;

    public AppointmentCreationStrategy getStrategy(User initiator) {
        return strategies.stream()
                .filter(s -> s.supports(initiator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Нет стратегии для пользователя"));
    }
}
