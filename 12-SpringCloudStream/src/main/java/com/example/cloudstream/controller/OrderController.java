package com.example.cloudstream.controller;

import com.example.cloudstream.dto.OrderEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final StreamBridge streamBridge;

    public OrderController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderEvent orderEvent) {
        boolean sent = streamBridge.send("raw-orders", orderEvent);
        if (sent) {
            return ResponseEntity.ok("Order sent successfully");
        } else {
            return ResponseEntity.internalServerError().body("Failed to send order");
        }
    }
}
