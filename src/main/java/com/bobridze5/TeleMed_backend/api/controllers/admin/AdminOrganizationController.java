package com.bobridze5.TeleMed_backend.api.controllers.admin;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationRequest;
import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationResponse;
import com.bobridze5.TeleMed_backend.api.dto.organization.MedicalOrganizationUpdateRequest;
import com.bobridze5.TeleMed_backend.core.service.organization.MedicalOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.ADMIN_ORGANIZATIONS)
@RequiredArgsConstructor
@Tag(name = "Медицинские организации (админ)", description = "Управление справочником организаций")
public class AdminOrganizationController {
    private final MedicalOrganizationService organizationService;

    @PostMapping
    @Operation(summary = "Создать организацию")
    public ResponseEntity<MedicalOrganizationResponse> create(
            @Valid @RequestBody MedicalOrganizationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить организацию")
    public MedicalOrganizationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody MedicalOrganizationUpdateRequest request
    ) {
        return organizationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить организацию")
    public void delete(@PathVariable Long id) {
        organizationService.delete(id);
    }
}
