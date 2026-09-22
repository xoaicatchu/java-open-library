package com.example.hibernatebatch.dto;

import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    BigDecimal price,
    boolean expired,
    String categoryName
) {}
