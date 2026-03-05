package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.data.ChangeUserDataRequest;
import com.bobridze5.TeleMed_backend.api.dto.data.ChangeUserDataResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UserResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UsersResponse;
import com.bobridze5.TeleMed_backend.core.service.auth.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.USERS)
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/")
    public UsersResponse getUsers(@RequestParam(value = "page") Integer pageNumber) {
        return userService.getUsers(pageNumber);
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable("userId") Long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{userId}")
    public ChangeUserDataResponse patchUserById(
            @PathVariable("userId") Long id,
            @Valid @RequestBody ChangeUserDataRequest request
    ) {
        return userService.changeUserData(id, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("userId") Long id) {
        userService.deleteUserById(id);
    }

}
