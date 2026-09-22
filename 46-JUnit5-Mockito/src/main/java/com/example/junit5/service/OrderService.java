package com.example.junit5.service;

import com.example.junit5.entity.Order;
import com.example.junit5.entity.OrderStatus;
import com.example.junit5.exception.OrderNotFoundException;
import com.example.junit5.exception.PaymentException;
import com.example.junit5.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository, PaymentGateway paymentGateway, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        
        boolean isPaid = paymentGateway.processPayment(order.getAmount());
        if (!isPaid) {
            savedOrder.setStatus(OrderStatus.FAILED);
            orderRepository.save(savedOrder);
            throw new PaymentException("Payment failed for order " + savedOrder.getId());
        }
        
        savedOrder.setStatus(OrderStatus.COMPLETED);
        notificationService.sendNotification("Order placed successfully", order.getCustomerName());
        return orderRepository.save(savedOrder);
    }
    
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setStatus(OrderStatus.CANCELLED);
        notificationService.sendNotification("Order cancelled", order.getCustomerName());
        return orderRepository.save(order);
    }
    
    public double calculateDiscount(double amount, int percentage) {
        if (percentage < 0 || percentage > 100) return amount;
        return amount - (amount * percentage / 100.0);
    }
}
