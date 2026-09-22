package com.example.webmvc.dto;

import com.example.webmvc.entity.Product;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO — chỉ expose những field cần thiết ra ngoài API.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        int stockQuantity,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * Factory method chuyển Entity -> Response DTO.
     */
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStockQuantity(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
