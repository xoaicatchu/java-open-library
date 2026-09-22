package com.example.integration.controller;

import com.example.integration.dto.OrderRequest;
import com.example.integration.dto.OrderResponse;
import com.example.integration.gateway.OrderGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderGateway orderGateway;

    public OrderController(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    @PostMapping
    public OrderResponse submitOrder(@RequestBody OrderRequest request) {
        return orderGateway.processOrder(request);
    }
}
