package com.example.integration.gateway;

import com.example.integration.dto.OrderRequest;
import com.example.integration.dto.OrderResponse;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway(defaultRequestChannel = "orderInputChannel")
public interface OrderGateway {
    
    @Gateway(requestChannel = "orderInputChannel")
    OrderResponse processOrder(OrderRequest order);
    
    // Allows sending with headers
    @Gateway(requestChannel = "orderInputChannel")
    OrderResponse processOrderMessage(Message<OrderRequest> orderMessage);
}
