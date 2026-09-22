package com.example.micrometer.controller;

import com.example.micrometer.dto.OrderRequest;
import com.example.micrometer.entity.Order;
import com.example.micrometer.service.OrderService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Timed(value = "api.orders.create.time", description = "Time taken to create an order via API", histogram = true)
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping
    @Timed(value = "api.orders.getall.time", description = "Time taken to fetch all orders", histogram = true)
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    @PostMapping("/users/login")
    public ResponseEntity<Void> login() {
        orderService.simulateUserLogin();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/users/logout")
    public ResponseEntity<Void> logout() {
        orderService.simulateUserLogout();
        return ResponseEntity.ok().build();
    }
}
