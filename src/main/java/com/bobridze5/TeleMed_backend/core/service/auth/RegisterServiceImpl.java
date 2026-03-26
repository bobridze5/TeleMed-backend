package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.api.mappers.auth.RegisterMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.VerificationToken;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityAlreadyExistsException;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;
    private final RegisterMapper registerMapper;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterPatientRequest request, String url) {
        log.info("Начало регистрации: email = {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Ошибка регистрации: email = {} уже существует", request.email());
            throw new EntityAlreadyExistsException("User with email = " + request.email() + " already exists");
        }

        Patient patient = registerMapper.mapToInitialPatient(request);
        patient = userRepository.save(patient);

        VerificationToken verificationToken = verificationTokenService.createToken(patient);
        emailService.sendVerificationToken(patient.getEmail(), url, verificationToken.getToken());

        log.info("Пользователь с id = {} зарегистрирован", patient.getId());
        return new RegisterResponse(patient.getId());
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterDoctorRequest request) {
        return null;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterAdminRequest request) {
        return null;
    }

}
