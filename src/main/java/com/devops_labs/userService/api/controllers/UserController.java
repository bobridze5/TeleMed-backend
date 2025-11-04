package com.devops_labs.userService.api.controllers;

import com.devops_labs.userService.api.dto.UserResponse;
import com.devops_labs.userService.api.dto.data.ChangeUserDataRequest;
import com.devops_labs.userService.api.dto.data.ChangeUserDataResponse;
import com.devops_labs.userService.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ChangeUserDataResponse> patchUserById(
            @PathVariable("userId") Long id,
            @RequestBody ChangeUserDataRequest request
    ) {
        ChangeUserDataResponse response = userService.changeUserData(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {

        return ResponseEntity.noContent().build();
    }

}
