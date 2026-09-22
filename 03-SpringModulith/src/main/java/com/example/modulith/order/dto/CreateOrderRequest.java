package com.example.modulith.order.dto;

import java.math.BigDecimal;

public record CreateOrderRequest(String customerName, BigDecimal totalAmount) {
}
