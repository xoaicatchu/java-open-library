package com.example.jdbi.domain;

public record Employee(
    Integer id,
    String name,
    Department department,
    Email email
) {}
