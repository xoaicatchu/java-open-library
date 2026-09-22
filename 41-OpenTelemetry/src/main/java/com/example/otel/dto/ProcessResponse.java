package com.example.otel.dto;

public record ProcessResponse(String status, String traceId, String spanId) {}
