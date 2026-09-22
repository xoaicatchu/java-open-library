package com.example.statemachine.dto;
import com.example.statemachine.domain.OrderState;
import java.util.List;

public record OrderDto(Long id, String description, OrderState state, List<HistoryDto> history) {}
