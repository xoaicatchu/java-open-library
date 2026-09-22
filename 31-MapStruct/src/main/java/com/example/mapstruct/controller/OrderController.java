package com.example.mapstruct.controller;

import com.example.mapstruct.dto.OrderResponse;
import com.example.mapstruct.entity.Customer;
import com.example.mapstruct.entity.Order;
import com.example.mapstruct.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderMapper orderMapper;

    public OrderController(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @PostMapping("/map")
    public OrderResponse mapOrder(@RequestBody Order order) {
        return orderMapper.toOrderResponse(order, order.getCustomer());
    }

    @GetMapping("/sample")
    public OrderResponse getSampleOrder() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        Order order = new Order();
        order.setId(100L);
        order.setOrderNumber("ORD-12345");
        order.setCustomer(customer);

        return orderMapper.toOrderResponse(order, customer);
    }
}
