package com.devops_labs.userService.api.controllers;

import com.devops_labs.userService.api.dto.CreateUserRequest;
import com.devops_labs.userService.api.dto.UserResponse;
import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<?> getUsers() {

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest body) {
        UserResponse response = userService.createUser(body);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }


//    @PatchMapping("/{userId}")
//    public ResponseEntity<User> changeUser(
//            @PathVariable Long id,
//            @RequestBody UserDto userDto
//    ) {
//
//        return null;
//    }
}
