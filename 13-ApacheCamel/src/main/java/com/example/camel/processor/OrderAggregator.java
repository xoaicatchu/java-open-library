package com.example.camel.processor;

import com.example.camel.dto.OrderResponse;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String enrichedItem = newExchange.getIn().getBody(String.class);

        if (oldExchange == null) {
            String orderId = newExchange.getProperty("originalOrderId", String.class);
            List<String> items = new ArrayList<>();
            items.add(enrichedItem);
            
            OrderResponse response = new OrderResponse(orderId, "Processed", items);
            newExchange.getIn().setBody(response);
            return newExchange;
        }

        OrderResponse currentResponse = oldExchange.getIn().getBody(OrderResponse.class);
        // Lists in records are immutable if created with List.of, but we used ArrayList above
        // Make sure it's mutable or re-create
        List<String> updatedItems = new ArrayList<>(currentResponse.processedItems());
        updatedItems.add(enrichedItem);
        
        OrderResponse updatedResponse = new OrderResponse(currentResponse.orderId(), currentResponse.status(), updatedItems);
        oldExchange.getIn().setBody(updatedResponse);
        return oldExchange;
    }
}
