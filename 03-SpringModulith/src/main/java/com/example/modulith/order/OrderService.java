package com.example.modulith.order;

import com.example.modulith.order.dto.CreateOrderRequest;
import com.example.modulith.order.dto.OrderResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.customerName());
        order.setTotalAmount(request.totalAmount());
        order.setStatus("CREATED");
        order = repository.save(order);
        events.publishEvent(new OrderCreatedEvent(order.getId(), order.getCustomerName(), order.getTotalAmount()));
        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(order.getId(), order.getCustomerName(), order.getTotalAmount(), order.getStatus());
    }
}
