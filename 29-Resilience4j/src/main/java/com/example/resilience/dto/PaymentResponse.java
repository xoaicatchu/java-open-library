package com.example.resilience.dto;

public record PaymentResponse(String transactionId, String status, String message) {}
