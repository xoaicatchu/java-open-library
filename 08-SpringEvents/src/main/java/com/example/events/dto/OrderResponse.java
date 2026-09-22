package com.example.events.dto;

public record OrderResponse(Long id, String productName, int quantity, String status) {}
