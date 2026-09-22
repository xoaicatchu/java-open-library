package com.example.openpdf.dto;

import java.math.BigDecimal;

public record ProductDto(
    String name,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal
) {}
