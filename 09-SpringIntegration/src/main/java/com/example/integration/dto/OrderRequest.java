package com.example.integration.dto;

import java.util.List;

public record OrderRequest(
    String orderId,
    String customerName,
    String orderType, // STANDARD, EXPRESS
    List<OrderItem> items
) {}
