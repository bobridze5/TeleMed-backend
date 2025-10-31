package com.devops_labs.userService.api.controllers;

import com.devops_labs.userService.api.dto.login.LoginUserRequest;
import com.devops_labs.userService.api.dto.login.LoginUserResponse;
import com.devops_labs.userService.api.dto.register.RegisterUserRequest;
import com.devops_labs.userService.api.dto.register.RegisterUserResponse;
import com.devops_labs.userService.core.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {
        RegisterUserResponse response = userAuthService.register(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
        LoginUserResponse response = userAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    // TODO: Переделать
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String accessToken) {
        return "типо вышли!";
    }

}
