package com.example.actuator.controller;

import com.example.actuator.endpoint.OrdersEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class DemoController {

    private final OrdersEndpoint ordersEndpoint;

    public DemoController(OrdersEndpoint ordersEndpoint) {
        this.ordersEndpoint = ordersEndpoint;
    }

    @GetMapping
    public ResponseEntity<Map<String, Integer>> getOrderStats() {
        return ResponseEntity.ok(ordersEndpoint.getOrderStats());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody Map<String, String> request) {
        String status = request.getOrDefault("status", "pending");
        ordersEndpoint.addOrder(status);
        return ResponseEntity.ok(Map.of("message", "Order recorded successfully", "status", status));
    }
}
