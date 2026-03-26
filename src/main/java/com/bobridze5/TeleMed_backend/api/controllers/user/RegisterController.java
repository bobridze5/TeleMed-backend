package com.bobridze5.TeleMed_backend.api.controllers.user;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterAdminRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterDoctorRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterPatientRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.core.service.auth.RegisterService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(API.AUTH_REGISTER)
public class RegisterController {
    private final RegisterService registerService;
    private final UrlBuilder urlBuilder;

    @PostMapping("/patient")
    public ResponseEntity<RegisterResponse> registerPatient(
            @Valid @RequestBody RegisterPatientRequest request,
            HttpServletRequest servletRequest
    ) {

        URI authBase = urlBuilder.buildAbsoluteUrl(servletRequest, API.AUTH_OLD);
        RegisterResponse response = registerService.register(request, authBase.toString());
        return ResponseEntity.created(authBase).body(response); // TODO: временно authBase
    }

    @PostMapping("/doctor")
    public RegisterResponse registerDoctor(
            @Valid @RequestBody RegisterDoctorRequest request
    ) {
        return registerService.register(request);
    }

    @PostMapping("/admin")
    public RegisterResponse registerAdmin(
            @Valid @RequestBody RegisterAdminRequest request
    ) {
        return registerService.register(request);
    }


}
