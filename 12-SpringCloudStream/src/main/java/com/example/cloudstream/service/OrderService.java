package com.example.cloudstream.service;

import com.example.cloudstream.entity.OrderEntity;
import com.example.cloudstream.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void saveOrder(OrderEntity entity) {
        orderRepository.save(entity);
    }
}
