package com.example.integration.config;

import com.example.integration.dto.OrderItem;
import com.example.integration.dto.OrderRequest;
import com.example.integration.dto.OrderResponse;
import com.example.integration.dto.ProcessedItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.router.PayloadTypeRouter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationConfig {

    // 1. Message Channels
    @Bean
    public MessageChannel orderInputChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public MessageChannel validatedOrderChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel standardOrderChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel expressOrderChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel enrichedOrderChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel processItemsChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public MessageChannel aggregatedChannel() {
        return new DirectChannel();
    }
    
    // Filter to discard empty orders
    @Filter(inputChannel = "orderInputChannel", outputChannel = "validatedOrderChannel")
    public boolean filterValidOrders(OrderRequest order) {
        return order.items() != null && !order.items().isEmpty();
    }

    // Router by payload content (orderType)
    @ServiceActivator(inputChannel = "validatedOrderChannel")
    @Bean
    public org.springframework.integration.router.AbstractMessageRouter orderRouter() {
        return new org.springframework.integration.router.AbstractMessageRouter() {
            @Override
            protected java.util.Collection<MessageChannel> determineTargetChannels(org.springframework.messaging.Message<?> message) {
                OrderRequest request = (OrderRequest) message.getPayload();
                if ("EXPRESS".equalsIgnoreCase(request.orderType())) {
                    return java.util.Collections.singletonList(expressOrderChannel());
                }
                return java.util.Collections.singletonList(standardOrderChannel());
            }
        };
    }
}
