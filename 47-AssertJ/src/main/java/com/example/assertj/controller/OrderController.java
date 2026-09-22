package com.example.assertj.controller;

import com.example.assertj.domain.Customer;
import com.example.assertj.domain.Order;
import com.example.assertj.domain.Product;
import org.assertj.core.api.SoftAssertions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping("/sample")
    public Order getSampleOrder() {
        Customer customer = new Customer("c1", "John Doe", "john@example.com");
        List<Product> products = List.of(
                new Product("p1", "Laptop", 1200.0),
                new Product("p2", "Mouse", 25.5)
        );
        return new Order(
                "o1",
                customer,
                products,
                Map.of("source", "web", "priority", "high"),
                LocalDateTime.now()
        );
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateOrder(@RequestBody Order order) {
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(order).isNotNull();
        if (order != null) {
            softly.assertThat(order.getOrderId()).isNotBlank();
            softly.assertThat(order.getCustomer()).isNotNull();
            if (order.getCustomer() != null) {
                softly.assertThat(order.getCustomer().getEmail()).contains("@");
            }
            softly.assertThat(order.getItems()).isNotEmpty();
            if (order.getItems() != null) {
                softly.assertThat(order.getItems()).allMatch(p -> p.getPrice() > 0, "Price > 0");
            }
        }

        try {
            softly.assertAll();
            return ResponseEntity.ok(Map.of("status", "valid", "message", "Order is valid"));
        } catch (AssertionError e) {
            return ResponseEntity.badRequest().body(Map.of("status", "invalid", "errors", e.getMessage()));
        }
    }
}
