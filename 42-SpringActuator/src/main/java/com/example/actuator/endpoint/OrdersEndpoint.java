package com.example.actuator.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Endpoint(id = "orders")
public class OrdersEndpoint {

    private final Map<String, Integer> orderStats = new ConcurrentHashMap<>();

    public OrdersEndpoint() {
        orderStats.put("totalOrders", 100);
        orderStats.put("pendingOrders", 5);
        orderStats.put("completedOrders", 95);
    }

    @ReadOperation
    public Map<String, Integer> getOrderStats() {
        return orderStats;
    }

    @WriteOperation
    public void addOrder(String status) {
        orderStats.compute("totalOrders", (k, v) -> (v == null ? 0 : v) + 1);
        if ("pending".equalsIgnoreCase(status)) {
            orderStats.compute("pendingOrders", (k, v) -> (v == null ? 0 : v) + 1);
        } else if ("completed".equalsIgnoreCase(status)) {
            orderStats.compute("completedOrders", (k, v) -> (v == null ? 0 : v) + 1);
        }
    }
}
