package com.bobridze5.TeleMed_backend.core.service.organization;

import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationRequest;
import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationResponse;
import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.City;
import com.bobridze5.TeleMed_backend.core.entity.medical.MedicalOrganization;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityAlreadyExistsException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.CityRepository;
import com.bobridze5.TeleMed_backend.core.repository.MedicalOrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalOrganizationService {
    private final MedicalOrganizationRepository organizationRepository;
    private final CityRepository cityRepository;

    public Page<MedicalOrganizationResponse> getAll(int page, int size) {
        return organizationRepository
                .findAll(PageRequest.of(page, size, Sort.by("name").ascending()))
                .map(this::toResponse);
    }

    public MedicalOrganizationResponse getById(Long id) {
        return toResponse(loadOrThrow(id));
    }

    @Transactional
    public MedicalOrganizationResponse create(MedicalOrganizationRequest request) {
        if (organizationRepository.existsByEmail(request.email())) {
            throw new EntityAlreadyExistsException("Организация с email = " + request.email() + " уже существует");
        }
        if (organizationRepository.existsByPhone(request.phone())) {
            throw new EntityAlreadyExistsException("Организация с телефоном = " + request.phone() + " уже существует");
        }

        MedicalOrganization organization = MedicalOrganization.builder()
                .name(request.name())
                .address(request.address())
                .email(request.email())
                .phone(request.phone())
                .city(loadCity(request.cityId()))
                .build();

        return toResponse(organizationRepository.save(organization));
    }

    @Transactional
    public MedicalOrganizationResponse update(Long id, MedicalOrganizationUpdateRequest request) {
        MedicalOrganization organization = loadOrThrow(id);

        if (request.name() != null && !request.name().isBlank()) {
            organization.setName(request.name());
        }
        if (request.address() != null && !request.address().isBlank()) {
            organization.setAddress(request.address());
        }
        if (request.email() != null && !request.email().isBlank() && !request.email().equals(organization.getEmail())) {
            if (organizationRepository.existsByEmail(request.email())) {
                throw new EntityAlreadyExistsException("Организация с email = " + request.email() + " уже существует");
            }
            organization.setEmail(request.email());
        }
        if (request.phone() != null && !request.phone().isBlank() && !request.phone().equals(organization.getPhone())) {
            if (organizationRepository.existsByPhone(request.phone())) {
                throw new EntityAlreadyExistsException("Организация с телефоном = " + request.phone() + " уже существует");
            }
            organization.setPhone(request.phone());
        }
        if (request.cityId() != null) {
            organization.setCity(loadCity(request.cityId()));
        }

        return toResponse(organizationRepository.save(organization));
    }

    @Transactional
    public void delete(Long id) {
        MedicalOrganization organization = loadOrThrow(id);
        organizationRepository.delete(organization);
    }

    private MedicalOrganization loadOrThrow(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Организация с id = " + id + " не найдена"));
    }

    private City loadCity(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new EntityNotFoundException("Город с id = " + cityId + " не найден"));
    }

    private MedicalOrganizationResponse toResponse(MedicalOrganization o) {
        City city = o.getCity();
        return new MedicalOrganizationResponse(
                o.getId(),
                o.getName(),
                o.getAddress(),
                o.getEmail(),
                o.getPhone(),
                city != null ? city.getId() : null,
                city != null ? city.getName() : null
        );
    }
}
