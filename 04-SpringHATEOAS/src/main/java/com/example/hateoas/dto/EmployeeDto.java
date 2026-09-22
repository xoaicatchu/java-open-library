package com.example.hateoas.dto;

public record EmployeeDto(
    Long id,
    String name,
    String role,
    String department,
    Double salary
) {}
