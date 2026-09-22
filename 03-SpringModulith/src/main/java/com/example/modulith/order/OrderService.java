package com.example.modulith.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository repository;
    private final ApplicationEventPublisher events;

    public OrderService(OrderRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public Order createOrder(String productCode, int quantity) {
        Order order = new Order();
        order.setProductCode(productCode);
        order.setQuantity(quantity);
        order = repository.save(order);
        events.publishEvent(new OrderCreatedEvent(order.getId(), productCode, quantity));
        return order;
    }
}
