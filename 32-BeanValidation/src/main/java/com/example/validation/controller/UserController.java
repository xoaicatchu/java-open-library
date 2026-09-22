package com.example.validation.controller;

import com.example.validation.dto.UserRegistrationRequest;
import com.example.validation.dto.ValidationGroups;
import com.example.validation.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated(ValidationGroups.OnCreate.class) @RequestBody UserRegistrationRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered");
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@Validated(ValidationGroups.OnUpdate.class) @RequestBody UserRegistrationRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok("User updated");
    }
}