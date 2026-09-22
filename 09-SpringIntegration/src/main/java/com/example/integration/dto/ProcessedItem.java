package com.example.integration.dto;

public record ProcessedItem(
    String itemId,
    String name,
    double totalLinePrice
) {}
