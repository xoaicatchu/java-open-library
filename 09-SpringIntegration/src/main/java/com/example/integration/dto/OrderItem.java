package com.example.integration.dto;

public record OrderItem(
    String itemId,
    String name,
    int quantity,
    double price
) {}
