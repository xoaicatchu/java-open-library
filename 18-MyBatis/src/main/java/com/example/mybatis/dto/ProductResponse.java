package com.example.mybatis.dto;

import com.example.mybatis.entity.Product;
import com.example.mybatis.entity.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    Long categoryId,
    ProductStatus status,
    CategoryResponse category
) {
    public static ProductResponse from(Product product) {
        if (product == null) return null;
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategoryId(),
            product.getStatus(),
            CategoryResponse.from(product.getCategory())
        );
    }
}
