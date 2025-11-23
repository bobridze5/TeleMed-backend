package com.devops_labs.userService.api.controllers;

import com.devops_labs.userService.api.dto.users.UserResponse;
import com.devops_labs.userService.api.dto.data.ChangeUserDataRequest;
import com.devops_labs.userService.api.dto.data.ChangeUserDataResponse;
import com.devops_labs.userService.api.dto.users.UsersResponse;
import com.devops_labs.userService.core.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
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
