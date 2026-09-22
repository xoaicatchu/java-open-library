package com.example.integration.service;

import com.example.integration.dto.OrderItem;
import com.example.integration.dto.OrderRequest;
import com.example.integration.dto.OrderResponse;
import com.example.integration.dto.ProcessedItem;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderProcessor {

    // Splitter: Breaks OrderRequest into a List of Messages (one for each Item)
    @Splitter(inputChannel = "enrichedOrderChannel", outputChannel = "processItemsChannel")
    public List<Message<OrderItem>> splitOrderIntoItems(Message<OrderRequest> message) {
        return message.getPayload().items().stream()
                .map(item -> MessageBuilder.withPayload(item)
                        .copyHeaders(message.getHeaders())
                        .build())
                .collect(Collectors.toList());
    }

    // ServiceActivator: Processes each item
    @ServiceActivator(inputChannel = "processItemsChannel", outputChannel = "aggregatedChannel")
    public Message<ProcessedItem> processItem(Message<OrderItem> itemMsg) {
        OrderItem item = itemMsg.getPayload();
        ProcessedItem processed = new ProcessedItem(item.itemId(), item.name(), item.quantity() * item.price());
        return MessageBuilder.withPayload(processed)
                .copyHeaders(itemMsg.getHeaders()) // carry forward headers, including correlation ID
                .build();
    }

    // Aggregator: Combines ProcessedItems back into OrderResponse
    @Aggregator(inputChannel = "aggregatedChannel") // automatically routes back to Gateway reply channel
    public Message<OrderResponse> aggregateItems(List<Message<ProcessedItem>> itemMessages) {
        if (itemMessages.isEmpty()) {
            return null;
        }

        // Get headers from first item (they should all have the original headers)
        Message<?> first = itemMessages.get(0);
        String shippingMethod = first.getHeaders().get("shippingMethod", String.class);
        Double shippingCost = first.getHeaders().get("shippingCost", Double.class);
        if (shippingCost == null) shippingCost = 0.0;
        
        OrderRequest originalOrder = first.getHeaders().get("originalOrder", OrderRequest.class); // if we stored it
        // Or get orderId from header or some default if not mapped. Let's say we assume correlationId
        
        List<ProcessedItem> items = itemMessages.stream()
                .map(Message::getPayload)
                .collect(Collectors.toList());
                
        double totalItemsCost = items.stream().mapToDouble(ProcessedItem::totalLinePrice).sum();
        
        OrderResponse response = new OrderResponse(
                "ORD-" + System.currentTimeMillis(),
                "PROCESSED",
                shippingMethod,
                totalItemsCost + shippingCost,
                items
        );
        
        return MessageBuilder.withPayload(response)
                .copyHeaders(first.getHeaders())
                .build();
    }
}
