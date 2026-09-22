package com.example.mapstruct.dto;

import java.math.BigDecimal;

public record ProductSummary(
    Long id, 
    String name, 
    BigDecimal price, 
    CategoryDto category, 
    String statusString,
    String formattedPrice
) {}
