package com.example.springdoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Phản hồi thông tin đơn hàng")
public record OrderResponse(
    @Schema(description = "ID đơn hàng", example = "100")
    Long id,
    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    String customerName,
    @Schema(description = "ID sản phẩm", example = "1")
    Long productId,
    @Schema(description = "Số lượng", example = "2")
    Integer quantity
) {}
