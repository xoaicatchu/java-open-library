package com.example.feign.dto;

public record ProductDto(
    Long id,
    String name,
    Double price
) {}
