package com.bobridze5.TeleMed_backend.api.controllers.admin;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.admin.AdminDoctorPendingResponse;
import com.bobridze5.TeleMed_backend.api.dto.admin.DoctorRejectRequest;
import com.bobridze5.TeleMed_backend.api.dto.doctor.DoctorResponse;
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

// TODO: закрыть через @PreAuthorize("hasRole('ADMIN')") когда будут реализованы роли
@RestController
@RequestMapping(API.ADMIN_DOCTORS)
@RequiredArgsConstructor
@Tag(name = "Администрирование", description = "Управление заявками врачей")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/pending")
    @Operation(summary = "Список врачей на проверке", description = "Возвращает врачей со статусом AWAITING_APPROVAL")
    public Page<AdminDoctorPendingResponse> getPendingDoctors(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC, size = 20) Pageable pageable
    ) {
        return adminService.getPendingDoctors(pageable);
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Одобрить заявку врача", description = "Переводит врача в статус ACTIVE и отправляет уведомление")
    public DoctorResponse approveDoctor(@PathVariable Long id) {
        return adminService.approveDoctor(id);
    }

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отклонить заявку врача", description = "Переводит врача в статус INACTIVE и отправляет уведомление с причиной")
    public void rejectDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRejectRequest request
    ) {
        adminService.rejectDoctor(id, request.reason());
    }
}
