package com.bobridze5.TeleMed_backend.core.config;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Admin;
import com.bobridze5.TeleMed_backend.core.repository.AdminRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email:admin@telemed.ru}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-password:Admin1234!}")
    private String defaultAdminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminRepository.count() == 0) {
            if (userRepository.existsByEmail(defaultAdminEmail)) {
                log.warn("Пользователь с email {} уже существует, но не является администратором. Пропуск создания.", defaultAdminEmail);
                return;
            }

            Admin admin = Admin.builder()
                    .email(defaultAdminEmail)
                    .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                    .firstName("Главный")
                    .lastName("Администратор")
                    .status(UserStatus.ACTIVE)
                    .build();

            adminRepository.save(admin);
            log.info("Создан администратор по умолчанию: {} / {}", defaultAdminEmail, defaultAdminPassword);
            log.warn("Смените пароль администратора после первого входа!");
        }
    }
}
