package com.example.modulith.order.dto;

import java.math.BigDecimal;

public record OrderResponse(Long id, String customerName, BigDecimal totalAmount, String status) {
}
