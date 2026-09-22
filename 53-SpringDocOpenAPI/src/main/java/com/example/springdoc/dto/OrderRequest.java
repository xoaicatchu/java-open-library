package com.example.springdoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Yêu cầu tạo đơn hàng")
public record OrderRequest(
    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    String customerName,
    @Schema(description = "ID sản phẩm", example = "1")
    Long productId,
    @Schema(description = "Số lượng", example = "2")
    Integer quantity
) {}
