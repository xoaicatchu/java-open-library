package com.example.validation.dto;

import com.example.validation.validation.PasswordMatch;
import com.example.validation.validation.PhoneNumber;
import com.example.validation.validation.StrongPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@PasswordMatch(groups = {ValidationGroups.OnCreate.class})
public record UserRegistrationRequest(
    @NotBlank(message = "Name is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    String name,

    @NotBlank(message = "Email is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Email(message = "Invalid email format", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    String email,

    @PhoneNumber(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    String phone,

    @StrongPassword(groups = {ValidationGroups.OnCreate.class})
    String password,

    String confirmPassword,
    
    @Min(value = 18, message = "Age must be at least 18", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Max(value = 120, message = "Age must be less than 120", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @NotNull(message = "Age is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    Integer age,

    @NotNull(message = "Address is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Valid
    AddressDto address
) {}