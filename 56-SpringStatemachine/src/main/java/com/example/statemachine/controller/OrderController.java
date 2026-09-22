package com.example.statemachine.controller;

import com.example.statemachine.domain.OrderEvent;
import com.example.statemachine.dto.HistoryDto;
import com.example.statemachine.dto.OrderDto;
import com.example.statemachine.entity.OrderEntity;
import com.example.statemachine.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestParam String description) {
        return ResponseEntity.ok(map(orderService.createOrder(description)));
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<OrderDto> triggerEvent(@PathVariable Long id, @RequestParam OrderEvent event) {
        return ResponseEntity.ok(map(orderService.sendEvent(id, event)));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(map(orderService.getOrder(id)));
    }

    private OrderDto map(OrderEntity entity) {
        var history = entity.getHistory().stream()
                .map(h -> new HistoryDto(h.getState(), h.getTimestamp().toString()))
                .toList();
        return new OrderDto(entity.getId(), entity.getDescription(), entity.getState(), history);
    }
}
