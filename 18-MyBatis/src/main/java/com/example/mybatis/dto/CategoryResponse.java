package com.example.mybatis.dto;

import com.example.mybatis.entity.Category;

public record CategoryResponse(
    Long id,
    String name,
    String description
) {
    public static CategoryResponse from(Category category) {
        if (category == null) return null;
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
