package com.bobridze5.TeleMed_backend.api.controllers.user;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.core.service.auth.RegisterService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(API.AUTH_REGISTER)
@Tag(
        name = "Регистрация пользователей",
        description = "Представлены три ручки для регистрации разных ролей"
)
public class RegisterController {
    private final RegisterService registerService;
    private final UrlBuilder urlBuilder;

    @PostMapping("/patient")
    @Operation(summary = "Регистрация пациента", description = "Создаёт учётную запись пациента и отправляет письмо для подтверждения email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пациент зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    public ResponseEntity<RegisterResponse> registerPatient(
            @Valid @RequestBody RegisterPatientRequest request,
            HttpServletRequest servletRequest
    ) {

        URI authBase = urlBuilder.buildAbsoluteUrl(servletRequest, API.AUTH);
        RegisterResponse response = registerService.register(request, authBase.toString());
        return ResponseEntity.created(authBase).body(response); // TODO: временно authBase
    }

    @PostMapping("/doctor")
    @Operation(
            summary = "Регистрация врача",
            description = "Создаёт заявку врача. После регистрации статус — AWAITING_APPROVAL. Войти можно только после одобрения администратором."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка врача принята на рассмотрение"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    public ResponseEntity<RegisterResponse> registerDoctor(
            @Valid @RequestBody RegisterDoctorRequest request
    ) {
        RegisterResponse response = registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin")
    @Operation(summary = "Регистрация администратора", description = "Создаёт учётную запись администратора")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    public RegisterResponse registerAdmin(
            @Valid @RequestBody RegisterAdminRequest request
    ) {
        return registerService.register(request);
    }


}
