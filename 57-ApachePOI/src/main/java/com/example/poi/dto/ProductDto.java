package com.example.poi.dto;

import java.math.BigDecimal;

public record ProductDto(
    String name,
    String category,
    BigDecimal price,
    Integer quantity
) {
}
