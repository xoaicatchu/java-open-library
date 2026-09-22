package com.example.integration.service;

import com.example.integration.dto.OrderRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderTransformer {

    @Transformer(inputChannel = "standardOrderChannel", outputChannel = "enrichedOrderChannel")
    public Message<OrderRequest> transformStandard(Message<OrderRequest> message) {
        return MessageBuilder.fromMessage(message)
                .setHeader("shippingMethod", "STANDARD_DELIVERY")
                .setHeader("shippingCost", 5.0)
                .build();
    }

    @Transformer(inputChannel = "expressOrderChannel", outputChannel = "enrichedOrderChannel")
    public Message<OrderRequest> transformExpress(Message<OrderRequest> message) {
        return MessageBuilder.fromMessage(message)
                .setHeader("shippingMethod", "EXPRESS_NEXT_DAY")
                .setHeader("shippingCost", 15.0)
                .build();
    }
}
