package com.example.amqp.service;

import com.example.amqp.config.RabbitConfig;
import com.example.amqp.dto.OrderNotification;
import com.example.amqp.dto.PaymentNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderNotification(OrderNotification notification) {
        log.info("Gửi thông báo đơn hàng: {}", notification);
        rabbitTemplate.convertAndSend(RabbitConfig.FANOUT_EXCHANGE, "", notification);
    }

    public void sendPaymentNotification(PaymentNotification notification) {
        log.info("Gửi thông báo thanh toán: {}", notification);
        rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, "payment.success", notification);
    }
    
    public String sendAndReceive(PaymentNotification notification) {
        log.info("Gửi và chờ phản hồi: {}", notification);
        Object response = rabbitTemplate.convertSendAndReceive(RabbitConfig.DIRECT_EXCHANGE, "payment.success", notification);
        return response != null ? response.toString() : "No response";
    }
}
