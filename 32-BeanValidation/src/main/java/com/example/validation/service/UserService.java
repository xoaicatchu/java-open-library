package com.example.validation.service;

import com.example.validation.dto.UserRegistrationRequest;
import com.example.validation.dto.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Set;
import jakarta.validation.constraints.Email;

@Service
@Validated
public class UserService {

    private final Validator validator;

    public UserService(Validator validator) {
        this.validator = validator;
    }

    public void registerUser(@Validated(ValidationGroups.OnCreate.class) UserRegistrationRequest request) {
    }

    public void updateUser(@Validated(ValidationGroups.OnUpdate.class) UserRegistrationRequest request) {
    }

    public void checkEmail(@Email String email) {
    }

    public void programmaticValidation(UserRegistrationRequest request) {
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request, ValidationGroups.OnCreate.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}