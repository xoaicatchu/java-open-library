package com.example.micrometer.service;

import com.example.micrometer.dto.OrderRequest;
import com.example.micrometer.entity.Order;
import com.example.micrometer.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MeterRegistry meterRegistry;
    
    // Metrics
    private final Counter orderCounter;
    private final DistributionSummary orderAmountSummary;
    private final Timer orderProcessingTimer;
    
    // Giả lập state
    private final AtomicInteger queueSize = new AtomicInteger(0);
    private final AtomicInteger activeUsers = new AtomicInteger(10); // Khởi tạo với 10 user giả định

    public OrderService(OrderRepository orderRepository, MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.meterRegistry = meterRegistry;
        
        // Khởi tạo các metrics
        // Counter: đếm số lượng đơn hàng, có tag theo loại sản phẩm
        this.orderCounter = Counter.builder("orders.created.total")
                .description("Total number of orders created")
                .register(meterRegistry);
                
        // DistributionSummary: phân phối giá trị đơn hàng
        this.orderAmountSummary = DistributionSummary.builder("orders.amount.distribution")
                .description("Distribution of order amounts")
                .baseUnit("USD")
                .register(meterRegistry);
                
        // Timer: đo thời gian xử lý đơn hàng
        this.orderProcessingTimer = Timer.builder("orders.processing.time")
                .description("Time taken to process an order")
                .register(meterRegistry);
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        queueSize.incrementAndGet();
        
        try {
            return orderProcessingTimer.record(() -> {
                // Tăng counter đếm số đơn
                meterRegistry.counter("orders.created.by.product", "product", request.productCode()).increment();
                orderCounter.increment();
                
                // Ghi nhận giá trị đơn hàng vào distribution summary
                if (request.amount() != null) {
                    orderAmountSummary.record(request.amount().doubleValue());
                }
                
                // Giả lập trễ xử lý
                try {
                    Thread.sleep((long) (Math.random() * 50));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Order order = new Order(request.productCode(), request.amount(), "CREATED", LocalDateTime.now());
                return orderRepository.save(order);
            });
        } finally {
            queueSize.decrementAndGet();
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    // Phương thức giả lập cho Gauge
    public int getQueueSize() {
        return queueSize.get();
    }
    
    public int getActiveUsersCount() {
        // Có thể thay đổi giá trị để test gauge
        return activeUsers.get();
    }
    
    public void simulateUserLogin() {
        activeUsers.incrementAndGet();
    }
    
    public void simulateUserLogout() {
        activeUsers.decrementAndGet();
    }
}
