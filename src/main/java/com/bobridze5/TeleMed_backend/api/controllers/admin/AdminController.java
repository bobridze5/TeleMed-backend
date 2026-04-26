package com.bobridze5.TeleMed_backend.api.controllers.admin;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorPendingResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminPatientResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminStatsResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.DoctorRejectRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Администрирование", description = "Управление пользователями системы")
public class AdminController {
    private final AdminService adminService;

    // ── Doctors ────────────────────────────────────────────────────────────────

    @GetMapping(API.ADMIN_DOCTORS + "/pending")
    @Operation(summary = "Список врачей на проверке")
    public Page<AdminDoctorPendingResponse> getPendingDoctors(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC, size = 20) Pageable pageable
    ) {
        return adminService.getPendingDoctors(pageable);
    }

    @GetMapping(API.ADMIN_DOCTORS)
    @Operation(summary = "Список всех врачей", description = "Опциональный фильтр по статусу (?status=ACTIVE|AWAITING_APPROVAL|BANNED|INACTIVE)")
    public Page<AdminDoctorResponse> getAllDoctors(
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 20) Pageable pageable
    ) {
        return adminService.getAllDoctors(status, pageable);
    }

    @PatchMapping(API.ADMIN_DOCTORS + "/{id}/approve")
    @Operation(summary = "Одобрить заявку врача")
    public DoctorResponse approveDoctor(@PathVariable Long id) {
        return adminService.approveDoctor(id);
    }

    @PatchMapping(API.ADMIN_DOCTORS + "/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отклонить заявку врача")
    public void rejectDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRejectRequest request) {
        adminService.rejectDoctor(id, request.reason());
    }

    // ── Patients ───────────────────────────────────────────────────────────────

    @GetMapping(API.ADMIN_PATIENTS)
    @Operation(summary = "Список всех пациентов")
    public Page<AdminPatientResponse> getAllPatients(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 20) Pageable pageable
    ) {
        return adminService.getAllPatients(pageable);
    }

    // ── Stats ──────────────────────────────────────────────────────────────────

    @GetMapping(API.ADMIN_STATS)
    @Operation(summary = "Статистика системы")
    public AdminStatsResponse getStats() {
        return adminService.getStats();
    }

    // ── Users (ban / activate) ────────────────────────────────────────────────

    @PatchMapping(API.ADMIN_USERS + "/{id}/ban")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Заблокировать пользователя")
    public void banUser(@PathVariable Long id) {
        adminService.banUser(id);
    }

    @PatchMapping(API.ADMIN_USERS + "/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Активировать пользователя")
    public void activateUser(@PathVariable Long id) {
        adminService.activateUser(id);
    }
}
