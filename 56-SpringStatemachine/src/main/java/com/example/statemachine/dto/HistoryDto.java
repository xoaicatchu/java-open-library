package com.example.statemachine.dto;
import com.example.statemachine.domain.OrderState;

public record HistoryDto(OrderState state, String timestamp) {}
