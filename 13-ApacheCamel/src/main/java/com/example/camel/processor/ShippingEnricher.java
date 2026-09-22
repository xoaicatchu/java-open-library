package com.example.camel.processor;

import com.example.camel.dto.OrderItem;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class ShippingEnricher implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        OrderItem item = exchange.getIn().getBody(OrderItem.class);
        if (item == null) {
            throw new IllegalArgumentException("OrderItem is null");
        }
        
        // Enrich item with shipping info
        String enrichedInfo = item.itemId() + " (Qty: " + item.quantity() + ") - Ready for Shipping";
        
        // Simulating error for error handler testing
        if (item.itemId().equals("error-item")) {
            throw new RuntimeException("Simulated processing error for item: " + item.itemId());
        }
        
        exchange.getIn().setBody(enrichedInfo);
    }
}
