package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationResponse;
import com.bobridze5.TeleMed_backend.core.service.organization.MedicalOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.ORGANIZATIONS)
@RequiredArgsConstructor
@Tag(name = "Медицинские организации", description = "Публичный справочник организаций (для регистрации врачей)")
public class MedicalOrganizationController {
    private final MedicalOrganizationService organizationService;

    @GetMapping
    @Operation(summary = "Список медицинских организаций")
    public Page<MedicalOrganizationResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return organizationService.getAll(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Организация по ID")
    public MedicalOrganizationResponse getById(@PathVariable Long id) {
        return organizationService.getById(id);
    }
}
