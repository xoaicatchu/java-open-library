package com.example.problemdetail.service;

import com.example.problemdetail.dto.OrderRequest;
import com.example.problemdetail.entity.Order;
import com.example.problemdetail.entity.OrderItem;
import com.example.problemdetail.exception.InsufficientStockException;
import com.example.problemdetail.exception.OrderAlreadyCancelledException;
import com.example.problemdetail.exception.OrderNotFoundException;
import com.example.problemdetail.exception.PaymentDeclinedException;
import com.example.problemdetail.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        for (var itemReq : request.items()) {
            if ("OUT_OF_STOCK_ITEM".equals(itemReq.productId())) {
                throw new InsufficientStockException(itemReq.productId());
            }
        }
        
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setStatus("CREATED");
        
        var items = request.items().stream().map(req -> {
            OrderItem item = new OrderItem();
            item.setProductId(req.productId());
            item.setQuantity(req.quantity());
            item.setPrice(req.price());
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);
        
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        
        return orderRepository.save(order);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrder(id);
        if ("CANCELLED".equals(order.getStatus())) {
            throw new OrderAlreadyCancelledException(id);
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
    
    @Transactional
    public void payOrder(Long id, String reasonCode) {
        Order order = getOrder(id);
        if (reasonCode != null && !reasonCode.isEmpty() && !reasonCode.equals("OK")) {
            throw new PaymentDeclinedException(reasonCode);
        }
        order.setStatus("PAID");
        orderRepository.save(order);
    }
}