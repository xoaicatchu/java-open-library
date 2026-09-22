package com.example.cloudstream.config;

import com.example.cloudstream.dto.EnrichedOrderEvent;
import com.example.cloudstream.dto.OrderEvent;
import com.example.cloudstream.entity.OrderEntity;
import com.example.cloudstream.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class StreamConfig {

    /**
     * Chuyển đổi OrderEvent thô thành EnrichedOrderEvent (có thêm phí ship và trạng thái)
     */
    @Bean
    public Function<OrderEvent, EnrichedOrderEvent> enrichOrder() {
        return payload -> {
            BigDecimal shippingFee = payload.amount().compareTo(new BigDecimal("100")) > 0 
                ? BigDecimal.ZERO : new BigDecimal("10");
            
            return new EnrichedOrderEvent(
                payload.orderId(),
                payload.customerId(),
                payload.amount(),
                shippingFee,
                "ENRICHED",
                payload.shouldFail()
            );
        };
    }

    /**
     * Tiêu thụ EnrichedOrderEvent và lưu vào DB
     */
    @Bean
    public Consumer<EnrichedOrderEvent> processOrder(com.example.cloudstream.service.OrderService orderService) {
        return payload -> {
            if (payload.shouldFail()) {
                throw new RuntimeException("Simulated processing failure for order: " + payload.orderId());
            }

            BigDecimal total = payload.amount().add(payload.shippingFee());
            OrderEntity entity = new OrderEntity(
                payload.orderId(),
                payload.customerId(),
                total,
                "PROCESSED"
            );
            orderService.saveOrder(entity);
        };
    }

    /**
     * Xử lý lỗi khi Consumer processOrder gặp exception
     */
    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<?> errorMessage) {
        System.err.println("Error processing message: " + errorMessage.getPayload());
        // Có thể lưu vào bảng DLQ, gửi cảnh báo, v.v.
    }
}
