package com.example.camel.controller;

import com.example.camel.dto.OrderRequest;
import com.example.camel.dto.OrderResponse;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/camel/orders")
public class OrderController {

    private final ProducerTemplate producerTemplate;

    public OrderController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> processOrder(@RequestBody OrderRequest request) {
        OrderResponse response = producerTemplate.requestBody("direct:processOrder", request, OrderResponse.class);
        return ResponseEntity.ok(response);
    }
}
