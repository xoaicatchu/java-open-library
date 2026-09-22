package com.example.webmvc.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request DTO cho tạo mới Product.
 * Dùng Java Record (JDK 16+) — immutable, tự sinh equals/hashCode/toString.
 */
public record CreateProductRequest(

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
        String name,

        @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
        String description,

        @NotNull(message = "Giá không được để trống")
        @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
        @Digits(integer = 10, fraction = 2, message = "Giá tối đa 10 chữ số nguyên, 2 thập phân")
        BigDecimal price,

        @NotBlank(message = "Danh mục không được để trống")
        @Size(max = 100, message = "Danh mục tối đa 100 ký tự")
        String category,

        @Min(value = 0, message = "Số lượng tồn kho không được âm")
        int stockQuantity
) {}
