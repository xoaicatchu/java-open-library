package com.example.camel.dto;
import java.util.List;
public record OrderResponse(String orderId, String status, List<String> processedItems) {}
