package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.api.mappers.auth.RegisterMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.VerificationToken;
import com.bobridze5.TeleMed_backend.core.entity.medical.*;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityAlreadyExistsException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AdminRepository;
import com.bobridze5.TeleMed_backend.core.repository.MedicalOrganizationRepository;
import com.bobridze5.TeleMed_backend.core.repository.SpecializationRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RegisterMapper registerMapper;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final SpecializationRepository specializationRepository;
    private final MedicalOrganizationRepository organizationRepository;

    @Transactional
    public RegisterResponse register(RegisterPatientRequest request, String url) {
        log.info("Начало регистрации пациента: email = {}", request.email());

        checkEmailExists(request.email());

        Patient patient = registerMapper.mapToInitialPatient(request);
        patient = userRepository.save(patient);

        VerificationToken verificationToken = verificationTokenService.createToken(patient);
        emailService.sendVerificationToken(patient.getEmail(), url, verificationToken.getToken());

        log.info("Пациент с id = {} зарегистрирован", patient.getId());
        return new RegisterResponse(patient.getId());
    }

    @Transactional
    public RegisterResponse register(RegisterDoctorRequest request) {
        log.info("Начало регистрации врача: email = {}", request.email());

        checkEmailExists(request.email());

        Specialization specialization = specializationRepository.findById(request.specializationId())
                .orElseThrow(() -> new EntityNotFoundException("Специализация не найдена"));

        MedicalOrganization organization = null;
        if (request.organizationId() != null) {
            organization = organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new EntityNotFoundException("Организация не найдена"));
        }

        Doctor doctor = registerMapper.mapToInitialDoctor(request, specialization, organization);
        doctor = userRepository.save(doctor);

        String doctorName = request.lastName() != null ? request.lastName() : request.email();
        emailService.sendDoctorApplicationReceived(doctor.getEmail(), doctorName);

        log.info("Врач с id = {} зарегистрирован, ожидает проверки", doctor.getId());
        return new RegisterResponse(doctor.getId());
    }

    @Transactional
    public RegisterResponse register(RegisterAdminRequest request) {
        log.info("Начало регистрации администратора: email = {}", request.email());

        checkEmailExists(request.email());

        Admin admin = registerMapper.mapToInitialAdmin(request);
        admin = adminRepository.save(admin);

        log.info("Администратор с id = {} зарегистрирован, ожидает проверки", admin.getId());
        return new RegisterResponse(admin.getId());
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Ошибка регистрации: email = {} уже существует", email);
            throw new EntityAlreadyExistsException("User with email = " + email + " already exists");
        }
    }
}
