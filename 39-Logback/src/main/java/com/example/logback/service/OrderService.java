package com.example.logback.service;

import com.example.logback.dto.OrderRequest;
import com.example.logback.util.LogMarkers;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public String createOrder(OrderRequest request) {
        log.info("Processing order request: {}", request);
        
        // Cố tình ghi log có sử dụng StructuredArguments để đưa vào JSON
        log.info("Order detail: {}", StructuredArguments.keyValue("order_qty", request.quantity()));

        if (request.quantity() > 100) {
            log.warn(LogMarkers.AUDIT, "Large order detected: {}", request);
        }

        String orderId = UUID.randomUUID().toString();
        
        log.info(LogMarkers.AUDIT, "Order created with ID: {}", orderId);
        
        return orderId;
    }
}
