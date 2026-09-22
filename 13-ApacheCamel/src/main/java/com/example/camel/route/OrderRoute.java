package com.example.camel.route;

import com.example.camel.dto.OrderRequest;
import com.example.camel.processor.OrderAggregator;
import com.example.camel.processor.ShippingEnricher;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {

    private final ShippingEnricher shippingEnricher;
    private final OrderAggregator orderAggregator;

    public OrderRoute(ShippingEnricher shippingEnricher, OrderAggregator orderAggregator) {
        this.shippingEnricher = shippingEnricher;
        this.orderAggregator = orderAggregator;
    }

    @Override
    public void configure() throws Exception {

        // Error Handler configuration
        errorHandler(deadLetterChannel("direct:deadLetter")
                .maximumRedeliveries(1)
                .redeliveryDelay(0)
                .logRetryAttempted(true));

        from("direct:deadLetter")
                .routeId("deadLetterRoute")
                .log(LoggingLevel.ERROR, "Failed to process message: ${exception.message}")
                .to("mock:deadLetter");

        // REST DSL
        rest("/orders")
                .post()
                .type(OrderRequest.class)
                .to("direct:restProcessOrder");

        from("direct:restProcessOrder")
                .routeId("restProcessOrderRoute")
                .to("direct:processOrder");

        // Main entry route
        from("direct:processOrder")
                .routeId("processOrderRoute")
                .log(LoggingLevel.INFO, "Received order ${body.orderId} of type ${body.type}")
                .setProperty("originalOrderId", simple("${body.orderId}"))
                // Content-Based Router
                .choice()
                    .when(simple("${body.type} == 'international'"))
                        .to("direct:internationalOrder")
                    .when(simple("${body.type} == 'express'"))
                        .to("direct:expressOrder")
                    .otherwise()
                        .to("direct:standardOrder")
                .end();

        from("direct:internationalOrder")
                .routeId("internationalOrderRoute")
                .log(LoggingLevel.INFO, "Routing to International Process")
                .to("direct:splitOrder");

        from("direct:expressOrder")
                .routeId("expressOrderRoute")
                .log(LoggingLevel.INFO, "Routing to Express Process")
                .to("direct:splitOrder");

        from("direct:standardOrder")
                .routeId("standardOrderRoute")
                .log(LoggingLevel.INFO, "Routing to Standard Process")
                .to("direct:splitOrder");

        // Splitter and Aggregator
        from("direct:splitOrder")
                .routeId("splitOrderRoute")
                .split(simple("${body.items}"), orderAggregator)
                    .to("direct:enrichItem")
                .end()
                .log(LoggingLevel.INFO, "Completed Aggregation for Order")
                .to("mock:result");

        // Transformer
        from("direct:enrichItem")
                .routeId("enrichItemRoute")
                .process(shippingEnricher)
                .log(LoggingLevel.INFO, "Item Enriched: ${body}");
    }
}
