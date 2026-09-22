package com.example.camel.dto;
import java.util.List;
public record OrderRequest(String orderId, String type, List<OrderItem> items) {}
