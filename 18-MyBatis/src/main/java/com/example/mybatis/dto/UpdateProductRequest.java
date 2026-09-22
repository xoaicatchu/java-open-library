package com.example.mybatis.dto;

import com.example.mybatis.entity.ProductStatus;

import java.math.BigDecimal;

public record UpdateProductRequest(
    String name,
    BigDecimal price,
    Long categoryId,
    ProductStatus status
) {
}
