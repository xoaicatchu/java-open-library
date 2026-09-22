package com.example.springdoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Yêu cầu tạo hoặc cập nhật Sản phẩm")
public record ProductRequest(
    @Schema(description = "Tên sản phẩm", example = "Laptop Dell XPS")
    String name,
    @Schema(description = "Giá sản phẩm", example = "25000000")
    BigDecimal price
) {}
