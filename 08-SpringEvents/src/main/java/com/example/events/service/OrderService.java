package com.example.events.service;

import com.example.events.dto.OrderRequest;
import com.example.events.dto.OrderResponse;
import com.example.events.entity.Order;
import com.example.events.event.GenericDomainEvent;
import com.example.events.event.OrderCancelledEvent;
import com.example.events.event.OrderCreatedEvent;
import com.example.events.repository.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order(request.productName(), request.quantity(), "CREATED");
        orderRepository.save(order);

        // Publish plain POJO event
        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId(), order.getProductName(), order.getQuantity()));
        
        // Publish generic event
        eventPublisher.publishEvent(new GenericDomainEvent<>("New order created", "INFO"));

        return new OrderResponse(order.getId(), order.getProductName(), order.getQuantity(), order.getStatus());
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        // Publish event
        eventPublisher.publishEvent(new OrderCancelledEvent(order.getId(), order.getProductName(), order.getQuantity()));

        return new OrderResponse(order.getId(), order.getProductName(), order.getQuantity(), order.getStatus());
    }
}
