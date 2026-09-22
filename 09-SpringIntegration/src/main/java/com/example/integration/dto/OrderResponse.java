package com.example.integration.dto;

import java.util.List;

public record OrderResponse(
    String orderId,
    String status,
    String shippingMethod,
    double totalAmount,
    List<ProcessedItem> processedItems
) {}
