package com.example.camunda.dto;

public record StartProcessRequest(
        String orderId,
        String customerName
) {
}
