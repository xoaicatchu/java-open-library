package com.example.springdoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Phản hồi thông tin Sản phẩm")
public record ProductResponse(
    @Schema(description = "ID sản phẩm", example = "1")
    Long id,
    @Schema(description = "Tên sản phẩm", example = "Laptop Dell XPS")
    String name,
    @Schema(description = "Giá sản phẩm", example = "25000000")
    BigDecimal price
) {}
