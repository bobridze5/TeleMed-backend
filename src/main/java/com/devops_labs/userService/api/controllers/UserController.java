package com.devops_labs.userService.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("{id}")
    public ResponseEntity<?> getSomething(){

        return null;
    }

    @PutMapping("{id}")
    public ResponseEntity<?> get(){

        return null;
    }
}
