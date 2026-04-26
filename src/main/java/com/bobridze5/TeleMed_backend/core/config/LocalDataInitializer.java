package com.bobridze5.TeleMed_backend.core.config;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Specialization;
import com.bobridze5.TeleMed_backend.core.repository.SpecializationRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {
    private static final String DEFAULT_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("doctor1@test.com")) {
            log.info("[LocalDataInitializer] Тестовые данные уже существуют, пропускаю");
            return;
        }

        String hash = passwordEncoder.encode(DEFAULT_PASSWORD);
        Specialization specialization = specializationRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        Doctor doctor1 = Doctor.builder()
                .email("doctor1@test.com")
                .passwordHash(hash)
                .firstName("Иван")
                .lastName("Петров")
                .status(UserStatus.ACTIVE)
                .specialization(specialization)
                .experience(5)
                .qualification("Высшая категория")
                .build();

        Doctor doctor2 = Doctor.builder()
                .email("doctor2@test.com")
                .passwordHash(hash)
                .firstName("Мария")
                .lastName("Сидорова")
                .status(UserStatus.ACTIVE)
                .specialization(specialization)
                .experience(3)
                .qualification("Первая категория")
                .build();

        Patient patient = Patient.builder()
                .email("patient@test.com")
                .passwordHash(hash)
                .firstName("Алексей")
                .lastName("Козлов")
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(doctor1);
        userRepository.save(doctor2);
        userRepository.save(patient);

        log.info("[LocalDataInitializer] Созданы тестовые пользователи:");
        log.info("doctor1@test.com  / {}", DEFAULT_PASSWORD);
        log.info("doctor2@test.com  / {}", DEFAULT_PASSWORD);
        log.info("patient@test.com  / {}", DEFAULT_PASSWORD);
    }
}
