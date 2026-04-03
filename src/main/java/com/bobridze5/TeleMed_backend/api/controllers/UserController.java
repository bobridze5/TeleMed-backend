package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.data.ChangeUserDataRequest;
import com.bobridze5.TeleMed_backend.api.dto.data.ChangeUserDataResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UserResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UsersResponse;
import com.bobridze5.TeleMed_backend.core.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/")
    public ResponseEntity<UsersResponse> getUsers(@RequestParam(value = "page") Integer pageNumber) {
        UsersResponse response = userService.getUsers(pageNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ChangeUserDataResponse> patchUserById(
            @PathVariable("userId") Long id,
            @Valid @RequestBody ChangeUserDataRequest request
    ) {
        ChangeUserDataResponse response = userService.changeUserData(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
